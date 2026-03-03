"""
Peptide Calculator - Flask Web Application
"""

import io
import csv
from datetime import datetime
from flask import Flask, render_template, request, redirect, url_for, send_file, session

from calculator import UserInputs, calculate, ADDITIONS

app = Flask(__name__)
app.secret_key = "peptide-calc-secret-key-2026"


@app.route("/", methods=["GET"])
def index():
    return render_template("index.html", additions=ADDITIONS)


@app.route("/calculate", methods=["POST"])
def calculate_route():
    try:
        inputs = UserInputs(
            budget_mensuel=float(request.form["budget_mensuel"]),
            poids_kg=float(request.form["poids_kg"]),
            age=int(request.form["age"]),
            cycle_semaines=int(request.form["cycle_semaines"]),
            addition=request.form["addition"].strip().lower(),
            freq_achat=request.form["freq_achat"].strip().lower(),
        )
    except (ValueError, KeyError) as e:
        return render_template("index.html", additions=ADDITIONS, error=f"Entrée invalide : {e}")

    if inputs.cycle_semaines < 8:
        return render_template(
            "index.html",
            additions=ADDITIONS,
            error="Le cycle doit être d'au moins 8 semaines.",
        )

    result = calculate(inputs)
    return render_template("results.html", result=result, now=datetime.now().strftime("%d/%m/%Y"))


@app.route("/export/csv", methods=["POST"])
def export_csv():
    try:
        inputs = UserInputs(
            budget_mensuel=float(request.form["budget_mensuel"]),
            poids_kg=float(request.form["poids_kg"]),
            age=int(request.form["age"]),
            cycle_semaines=int(request.form["cycle_semaines"]),
            addition=request.form["addition"].strip().lower(),
            freq_achat=request.form["freq_achat"].strip().lower(),
        )
    except (ValueError, KeyError):
        return redirect(url_for("index"))

    result = calculate(inputs)

    output = io.StringIO()
    writer = csv.writer(output)

    writer.writerow(["=== PROTOCOLE FEMME DÉBUTANTE ==="])
    writer.writerow(["Semaines", "Retatrutide (mg/sem)", "Tesamorelin (mg/jour)", "Addition (mg/sem)"])
    for p in result.protocol:
        writer.writerow([p.semaines, p.reta_mg_sem, p.tesa_mg_jour, p.addition_mg_sem])

    writer.writerow([])
    writer.writerow(["=== COÛTS PAR VENDOR ==="])
    writer.writerow(["Vendor", "Total Cycle ($)", "Mensuel ($)", "$/mg Reta", "Shipping+Taxes ($)", "Dans Budget", "URL"])
    for v in result.vendor_costs:
        writer.writerow([
            v.vendor,
            v.total_cycle,
            v.mensuel,
            v.cost_per_mg,
            v.shipping_taxes,
            "OUI" if v.dans_budget else "NON",
            v.url,
        ])

    output.seek(0)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M")
    return send_file(
        io.BytesIO(output.getvalue().encode("utf-8-sig")),
        mimetype="text/csv",
        as_attachment=True,
        download_name=f"peptides_{timestamp}.csv",
    )


if __name__ == "__main__":
    # use_reloader=False avoids fork issues on iSH / Alpine Linux
    app.run(debug=True, port=5000, use_reloader=False)
