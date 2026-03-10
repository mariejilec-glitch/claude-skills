#!/usr/bin/env python3
"""Peptide Calculator 2026 · stdlib only · zero pip/apk required"""

from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import parse_qs
import csv, io, html as E

TAX = 0.14975
BAC = 2.0
VENDORS = {
    "GrowthGuys.ca": {
        "Reta": {10:90, 20:150, 40:225, 60:300}, "Tesa": {10:85, 20:150},
        "ship": 19.99, "free": 500, "url": "https://growthguys.ca/shop/retatrutide/",
    },
    "GreatNorthernPeptides.com": {
        "Reta": {40:225}, "Tesa": {10:75},
        "ship": 18, "free": 0, "url": "https://greatnorthernpeptides.com",
    },
    "PolarPeptides.ca": {
        "MOTS-c": {10:64.99, 40:199},
        "ship": 15, "free": 300, "url": "https://polarpeptides.ca/products/mots-c",
    },
    "LJSynergy.ca": {
        "Reta": {10:130}, "Tesa": {10:110},
        "ship": 20, "free": 0, "url": "https://ljsynergy.ca",
    },
}

# ── calculation ─────────────────────────────────────────────────────────────
def calc(budget, poids, age, weeks, addition):
    base  = 0.25 if poids < 60 else 0.5
    doses = [base, base * 2, min(4.0, poids / 15)]
    phases = [4, 4, weeks - 8] if weeks > 8 else [weeks // 3] * 3

    total_reta = sum(doses[i] * phases[i] for i in range(3))
    total_tesa = (0.5 if age > 35 else 0.75) * 7 * weeks
    total_add  = 10 * weeks if "mots" in addition else 0

    protocol, start = [], 1
    for i, (d, p) in enumerate(zip(doses, phases)):
        protocol.append({"sem": f"{start}\u2013{start+p-1}", "reta": d,
                         "tesa": [0.5, 0.75, 0.75][i],
                         "add": 5 if "mots" in addition else 0})
        start += p

    recon = [{"mg": mg, "conc": (c := round(mg / BAC, 2)),
              "x1": round(1 / c, 3), "x4": round(4 / c, 3)}
             for mg in [5, 10, 20, 30, 40, 50]]

    add_label = addition.upper() if addition not in ("aucune", "") else "Addition"
    consumption = [
        {"prod": "Retatrutide", "total": total_reta, "vials": round(total_reta / 40, 1)},
        {"prod": "Tesamorelin", "total": total_tesa, "vials": round(total_tesa / 10, 1)},
        {"prod": add_label,     "total": total_add,  "vials": round(total_add  / 10, 1)},
    ]

    vendors = []
    for name, info in VENDORS.items():
        sub = 0.0
        if "Reta" in info and total_reta > 0:
            best = max(info["Reta"], key=lambda k: info["Reta"][k] / k)
            sub += -(-total_reta // best) * info["Reta"][best]
        if "Tesa" in info and total_tesa > 0:
            sub += round(total_tesa / 10) * list(info["Tesa"].values())[0]
        if "mots" in addition and "MOTS-c" in info and total_add > 0:
            sub += round(total_add / 10) * info["MOTS-c"][10]
        if not sub:
            continue
        ship  = 0.0 if sub >= info["free"] else info["ship"]
        total = sub + ship + (sub + ship) * TAX
        monthly = round(total * 4.33 / weeks, 2)
        vendors.append({"name": name, "total": round(total, 2), "monthly": monthly,
                        "per_mg": round(sub / total_reta, 2) if total_reta else 0,
                        "ship_tax": round(ship + (sub + ship) * TAX, 2),
                        "ok": monthly <= budget, "url": info["url"]})

    best = next((v["name"] for v in vendors if v["ok"]), None)
    return dict(protocol=protocol, recon=recon, consumption=consumption,
                vendors=vendors, best=best,
                budget=budget, poids=poids, age=age, weeks=weeks, addition=addition)

# ── shared CSS ───────────────────────────────────────────────────────────────
CSS = """
*{box-sizing:border-box;margin:0;padding:0}
body{font-family:system-ui,sans-serif;background:#0f0f1a;color:#e2e8f0;padding:2rem 1rem}
.card{background:#1a1a2e;border:1px solid #2d2d4e;border-radius:1rem;padding:2rem;
  max-width:520px;margin:0 auto}
h1{color:#a78bfa;font-size:1.4rem;margin-bottom:.25rem}
.sub{color:#94a3b8;font-size:.85rem;margin-bottom:1.5rem}
.field{margin-bottom:1rem}
label{display:block;font-size:.85rem;color:#cbd5e1;margin-bottom:.3rem}
input,select{width:100%;background:#0f0f1a;border:1px solid #334155;border-radius:.5rem;
  color:#e2e8f0;padding:.6rem .8rem;font-size:.9rem}
.g2{display:grid;grid-template-columns:1fr 1fr;gap:1rem}
button{width:100%;background:linear-gradient(135deg,#7c3aed,#a855f7);color:#fff;
  border:none;border-radius:.5rem;padding:.8rem;font-size:1rem;font-weight:600;
  cursor:pointer;margin-top:.5rem}
.container{max-width:900px;margin:0 auto}
h2{color:#c4b5fd;font-size:1rem;margin:1.5rem 0 .6rem;
  border-bottom:1px solid #2d2d4e;padding-bottom:.3rem}
table{width:100%;border-collapse:collapse;font-size:.85rem;background:#1a1a2e;
  border-radius:.75rem;overflow:hidden;margin-bottom:1rem}
th{background:#2d2d4e;color:#94a3b8;text-align:left;padding:.55rem .8rem;
  font-size:.75rem;text-transform:uppercase;letter-spacing:.04em}
td{padding:.55rem .8rem;border-top:1px solid #1e1e38}
.yes{background:#14532d;color:#86efac;padding:.1rem .45rem;
  border-radius:999px;font-size:.75rem;font-weight:600}
.no{background:#450a0a;color:#fca5a5;padding:.1rem .45rem;
  border-radius:999px;font-size:.75rem;font-weight:600}
.best td{background:#1b1b3a;border-left:3px solid #a78bfa}
a{color:#818cf8;font-size:.8rem}
.acts{display:flex;gap:.75rem;margin-bottom:1.5rem;flex-wrap:wrap}
.btn{padding:.55rem 1.1rem;border-radius:.5rem;font-size:.85rem;font-weight:600;
  cursor:pointer;border:1px solid #334155;background:#1e293b;color:#e2e8f0;
  text-decoration:none;display:inline-block}
.notice{background:#1e1b4b;border:1px solid #4338ca;border-radius:.5rem;
  padding:.7rem 1rem;font-size:.8rem;color:#a5b4fc;margin-bottom:1.5rem}
.sum{background:#1a1a2e;border:1px solid #2d2d4e;border-radius:.75rem;
  padding:1rem 1.5rem;margin-bottom:1.5rem;display:flex;gap:2rem;flex-wrap:wrap}
.sum .lbl{font-size:.7rem;color:#64748b;text-transform:uppercase;letter-spacing:.05em}
.sum .val{font-size:1.05rem;font-weight:700;color:#a78bfa;margin-top:.15rem}
"""

FORM_HTML = f"""<!doctype html><html lang="fr"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Peptide Calculator 2026</title><style>{CSS}</style></head><body>
<div class="card">
<h1>&#10084; Peptide Calculator 2026</h1>
<p class="sub">Protocole femme d&eacute;butante &middot; Sources CA</p>
<form method="POST" action="/calculate">
<div class="g2">
  <div class="field"><label>Budget mensuel ($)</label>
    <input name="budget_mensuel" type="number" min="50" step="10" placeholder="200" required></div>
  <div class="field"><label>Poids (kg)</label>
    <input name="poids_kg" type="number" min="40" step="0.5" placeholder="65" required></div>
</div>
<div class="g2">
  <div class="field"><label>&Acirc;ge</label>
    <input name="age" type="number" min="18" max="99" placeholder="35" required></div>
  <div class="field"><label>Cycle (semaines)</label>
    <input name="cycle_semaines" type="number" min="8" max="52" value="12" required></div>
</div>
<div class="field"><label>Addition</label>
<select name="addition">
  <option value="aucune">Aucune</option>
  <option value="mots-c">MOTS-C</option>
  <option value="cjc-dac">CJC-DAC</option>
  <option value="nmn_oral">NMN Oral</option>
</select></div>
<div class="field"><label>Fr&eacute;quence d&apos;achat</label>
<select name="freq_achat">
  <option value="mensuel">Mensuel</option>
  <option value="bihebdo">Bihebdomadaire</option>
  <option value="hebdo">Hebdomadaire</option>
</select></div>
<button type="submit">Calculer &#8594;</button>
</form></div></body></html>"""


def results_html(r):
    best = r["best"]
    bv   = next((v for v in r["vendors"] if v["name"] == best), None)

    summary = ""
    if bv:
        summary = (f'<div class="sum">'
                   f'<div><div class="lbl">Meilleur vendor</div>'
                   f'<div class="val">{E.escape(bv["name"])}</div></div>'
                   f'<div><div class="lbl">Mensuel</div>'
                   f'<div class="val">{bv["monthly"]} $</div></div>'
                   f'<div><div class="lbl">Total cycle</div>'
                   f'<div class="val">{bv["total"]} $</div></div></div>')

    add_label = r["addition"].upper() if r["addition"] not in ("aucune", "") else "Addition"

    proto = "".join(
        f"<tr><td>{p['sem']}</td><td>{p['reta']}</td>"
        f"<td>{p['tesa']}</td><td>{p['add']}</td></tr>"
        for p in r["protocol"]
    )
    recon = "".join(
        f"<tr><td>{x['mg']}</td><td>{x['conc']}</td>"
        f"<td>{x['x1']}</td><td>{x['x4']}</td></tr>"
        for x in r["recon"]
    )
    cons = "".join(
        f"<tr><td>{c['prod']}</td><td>{c['total']}</td><td>{c['vials']}</td></tr>"
        for c in r["consumption"]
    )
    vend = ""
    for v in r["vendors"]:
        cls   = ' class="best"' if v["name"] == best else ""
        star  = " &#9733;" if v["name"] == best else ""
        badge = '<span class="yes">OUI</span>' if v["ok"] else '<span class="no">NON</span>'
        vend += (f'<tr{cls}><td>{E.escape(v["name"])}{star}</td>'
                 f'<td>{v["total"]} $</td><td>{v["monthly"]} $</td>'
                 f'<td>{v["per_mg"]}</td><td>{v["ship_tax"]} $</td>'
                 f'<td>{badge}</td>'
                 f'<td><a href="{E.escape(v["url"])}" target="_blank">Prix live &#8599;</a></td></tr>')

    csv_form = (
        f'<form method="POST" action="/export/csv" style="display:inline">'
        f'<input type="hidden" name="budget_mensuel" value="{r["budget"]}">'
        f'<input type="hidden" name="poids_kg" value="{r["poids"]}">'
        f'<input type="hidden" name="age" value="{r["age"]}">'
        f'<input type="hidden" name="cycle_semaines" value="{r["weeks"]}">'
        f'<input type="hidden" name="addition" value="{E.escape(r["addition"])}">'
        f'<input type="hidden" name="freq_achat" value="mensuel">'
        f'<button type="submit" class="btn">&#8595; Exporter CSV</button></form>'
    )

    return f"""<!doctype html><html lang="fr"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>R&eacute;sultats &middot; Peptide Calculator</title><style>{CSS}</style></head><body>
<div class="container">
<h1>&#10084; R&eacute;sultats</h1>
<p class="sub">Budget {r['budget']}$/mois &middot; {r['poids']} kg &middot; {r['age']} ans &middot; {r['weeks']} semaines</p>
<div class="notice">&#8505; Informations &eacute;ducatives uniquement. Consulte un professionnel de sant&eacute;.</div>
{summary}
<div class="acts"><a href="/" class="btn">&#8592; Nouveau calcul</a>{csv_form}</div>

<h2>1 &middot; Protocole Femme D&eacute;butante</h2>
<table><thead><tr><th>Semaines</th><th>Retatrutide mg/sem</th>
<th>Tesamorelin mg/jour</th><th>{E.escape(add_label)} mg/sem</th></tr></thead>
<tbody>{proto}</tbody></table>

<h2>2 &middot; Guide Reconstitution (2 ml BAC water)</h2>
<table><thead><tr><th>Vial (mg)</th><th>Conc. mg/ml</th>
<th>1 mg &rarr; ml</th><th>4 mg &rarr; ml</th></tr></thead>
<tbody>{recon}</tbody></table>

<h2>3 &middot; Consommation Totale</h2>
<table><thead><tr><th>Produit</th><th>Total mg</th><th>Vials requis</th></tr></thead>
<tbody>{cons}</tbody></table>

<h2>4 &middot; Co&ucirc;ts par Vendor</h2>
<table><thead><tr><th>Vendor</th><th>Total Cycle</th><th>Mensuel</th>
<th>$/mg Reta</th><th>Ship+Taxes</th><th>Budget</th><th>Source</th></tr></thead>
<tbody>{vend}</tbody></table>
</div></body></html>"""


# ── HTTP handler ─────────────────────────────────────────────────────────────
def _parse(handler):
    n = int(handler.headers.get("Content-Length", 0))
    body = handler.rfile.read(n).decode()
    data = parse_qs(body)
    return lambda k, d="": data.get(k, [d])[0]


class Handler(BaseHTTPRequestHandler):
    def _html(self, body, status=200):
        b = body.encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "text/html; charset=utf-8")
        self.send_header("Content-Length", len(b))
        self.end_headers()
        self.wfile.write(b)

    def do_GET(self):
        self._html(FORM_HTML)

    def do_POST(self):
        g = _parse(self)
        try:
            budget = float(g("budget_mensuel", "200"))
            poids  = float(g("poids_kg", "65"))
            age    = int(g("age", "35"))
            weeks  = int(g("cycle_semaines", "12"))
            addition = g("addition", "aucune").strip().lower()
        except ValueError:
            self._html(FORM_HTML)
            return

        if self.path == "/calculate":
            self._html(results_html(calc(budget, poids, age, weeks, addition)))

        elif self.path == "/export/csv":
            r = calc(budget, poids, age, weeks, addition)
            out = io.StringIO()
            w = csv.writer(out)
            w.writerow(["Semaines", "Reta mg/sem", "Tesa mg/jour", "Add mg/sem"])
            for p in r["protocol"]:
                w.writerow([p["sem"], p["reta"], p["tesa"], p["add"]])
            w.writerow([])
            w.writerow(["Vendor","Total","Mensuel","$/mg","Ship+Tax","Budget","URL"])
            for v in r["vendors"]:
                w.writerow([v["name"], v["total"], v["monthly"], v["per_mg"],
                            v["ship_tax"], "OUI" if v["ok"] else "NON", v["url"]])
            b = out.getvalue().encode("utf-8-sig")
            self.send_response(200)
            self.send_header("Content-Type", "text/csv; charset=utf-8")
            self.send_header("Content-Disposition", "attachment; filename=peptides.csv")
            self.send_header("Content-Length", len(b))
            self.end_headers()
            self.wfile.write(b)
        else:
            self._html(FORM_HTML, 404)

    def log_message(self, *_): pass  # silence request logs


# ── entry point ───────────────────────────────────────────────────────────────
if __name__ == "__main__":
    port = 5000
    print(f"\n  Peptide Calculator pret !")
    print(f"  Ouvre Safari -> http://localhost:{port}\n")
    HTTPServer(("", port), Handler).serve_forever()
