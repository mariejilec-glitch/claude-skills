"""
Peptide Calculator - Core Logic
Femme débutante optimized protocol calculator
"""

from dataclasses import dataclass, field
from typing import Optional


TAX_RATE = 0.14975
BAC_ML = 2.0

PRICE_DB = {
    "GrowthGuys.ca": {
        "Reta": {10: 90, 20: 150, 40: 225, 60: 300},
        "Tesa": {10: 85, 20: 150},
        "BAC": 20,
        "shipping": 19.99,
        "free_over": 500,
        "url": "https://growthguys.ca/shop/retatrutide/",
    },
    "GreatNorthernPeptides.com": {
        "Reta": {40: 225},
        "Tesa": {10: 75},
        "BAC": 20,
        "shipping": 18,
        "free_over": 0,
        "url": "https://greatnorthernpeptides.com",
    },
    "PolarPeptides.ca": {
        "MOTS-c": {10: 64.99, 40: 199},
        "shipping": 15,
        "free_over": 300,
        "url": "https://polarpeptides.ca/products/mots-c",
    },
    "LJSynergy.ca": {
        "Reta": {10: 130},
        "Tesa": {10: 110},
        "shipping": 20,
        "free_over": 0,
        "url": "https://ljsynergy.ca",
    },
}

ADDITIONS = ("mots-c", "cjc-dac", "nmn_oral", "aucune")


@dataclass
class UserInputs:
    budget_mensuel: float
    poids_kg: float
    age: int
    cycle_semaines: int
    addition: str
    freq_achat: str


@dataclass
class ProtocolPhase:
    semaines: str
    reta_mg_sem: float
    tesa_mg_jour: float
    addition_mg_sem: float


@dataclass
class ReconstitutionRow:
    vial_mg: int
    concentration_mg_ml: float
    one_mg_ml: float
    four_mg_ml: float


@dataclass
class ConsumptionRow:
    produit: str
    total_mg: float
    vials_requis: float


@dataclass
class VendorCost:
    vendor: str
    total_cycle: float
    mensuel: float
    cost_per_mg: float
    shipping_taxes: float
    dans_budget: bool
    url: str


@dataclass
class CalculationResult:
    protocol: list[ProtocolPhase]
    reconstitution: list[ReconstitutionRow]
    consumption: list[ConsumptionRow]
    vendor_costs: list[VendorCost]
    best_vendor: Optional[str]
    inputs: UserInputs


def _titration_phases(poids_kg: float, cycle_semaines: int) -> tuple[list[float], list[int]]:
    base_start = 0.25 if poids_kg < 60 else 0.5
    doses = [base_start, base_start * 2, min(4.0, poids_kg / 15)]
    if cycle_semaines > 8:
        phases = [4, 4, cycle_semaines - 8]
    else:
        phases = [cycle_semaines // 3] * 3
    return doses, phases


def _compute_protocol(inputs: UserInputs) -> tuple[list[ProtocolPhase], float, float, float]:
    doses, phases = _titration_phases(inputs.poids_kg, inputs.cycle_semaines)
    tesa_daily = 0.5 if inputs.age > 35 else 0.75

    total_reta = sum(doses[i] * phases[i] for i in range(3))
    total_tesa = tesa_daily * 7 * inputs.cycle_semaines
    total_add = 10 * inputs.cycle_semaines if "mots" in inputs.addition else 0

    add_label = inputs.addition.capitalize() if inputs.addition != "aucune" else "Addition"
    add_per_phase = 5 if "mots" in inputs.addition else 0

    protocol = []
    start = 1
    tesa_phases = [0.5, 0.75, 0.75]
    for i in range(3):
        end = start + phases[i] - 1
        protocol.append(ProtocolPhase(
            semaines=f"{start}-{end}",
            reta_mg_sem=doses[i],
            tesa_mg_jour=tesa_phases[i],
            addition_mg_sem=add_per_phase,
        ))
        start = end + 1

    return protocol, total_reta, total_tesa, total_add


def _compute_reconstitution() -> list[ReconstitutionRow]:
    rows = []
    for vial_mg in [5, 10, 20, 30, 40, 50]:
        concentration = round(vial_mg / BAC_ML, 2)
        rows.append(ReconstitutionRow(
            vial_mg=vial_mg,
            concentration_mg_ml=concentration,
            one_mg_ml=round(1 / concentration, 3),
            four_mg_ml=round(4 / concentration, 3),
        ))
    return rows


def _compute_vendor_costs(
    inputs: UserInputs,
    total_reta: float,
    total_tesa: float,
    total_add: float,
) -> list[VendorCost]:
    results = []

    for vendor_name, info in PRICE_DB.items():
        subtotal = 0.0

        if "Reta" in info and total_reta > 0:
            best_vial = max(info["Reta"], key=lambda k: info["Reta"][k] / k)
            vials_reta = -(-total_reta // best_vial)  # ceiling division
            subtotal += vials_reta * info["Reta"][best_vial]

        if "Tesa" in info and total_tesa > 0:
            vials_tesa = round(total_tesa / 10)
            subtotal += vials_tesa * list(info["Tesa"].values())[0]

        if "mots" in inputs.addition and "MOTS-c" in info and total_add > 0:
            vials_add = round(total_add / 10)
            subtotal += vials_add * info["MOTS-c"][10]

        if subtotal == 0:
            continue

        shipping = 0.0 if subtotal >= info.get("free_over", 9999) else info["shipping"]
        taxes = (subtotal + shipping) * TAX_RATE
        total_order = subtotal + shipping + taxes
        monthly = round(total_order * (4.33 / inputs.cycle_semaines), 2)
        cost_per_mg = round(subtotal / total_reta, 2) if total_reta else 0

        results.append(VendorCost(
            vendor=vendor_name,
            total_cycle=round(total_order, 2),
            mensuel=monthly,
            cost_per_mg=cost_per_mg,
            shipping_taxes=round(shipping + taxes, 2),
            dans_budget=monthly <= inputs.budget_mensuel,
            url=info["url"],
        ))

    return results


def calculate(inputs: UserInputs) -> CalculationResult:
    protocol, total_reta, total_tesa, total_add = _compute_protocol(inputs)
    reconstitution = _compute_reconstitution()

    add_label = inputs.addition.capitalize() if inputs.addition != "aucune" else "Addition"
    consumption = [
        ConsumptionRow("Retatrutide", total_reta, round(total_reta / 40, 1)),
        ConsumptionRow("Tesamorelin", total_tesa, round(total_tesa / 10, 1)),
        ConsumptionRow(add_label, total_add, round(total_add / 10, 1)),
    ]

    vendor_costs = _compute_vendor_costs(inputs, total_reta, total_tesa, total_add)

    in_budget = [v for v in vendor_costs if v.dans_budget]
    best_vendor = in_budget[0].vendor if in_budget else None

    return CalculationResult(
        protocol=protocol,
        reconstitution=reconstitution,
        consumption=consumption,
        vendor_costs=vendor_costs,
        best_vendor=best_vendor,
        inputs=inputs,
    )
