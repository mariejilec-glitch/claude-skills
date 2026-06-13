# Hypothesis Testing

## Core Framework

Every hypothesis test follows the same structure:

1. **H₀ (null hypothesis):** The default assumption (no effect, no difference)
2. **H₁ (alternative hypothesis):** The claim you want to support
3. **α (significance level):** Acceptable false positive rate (typically 0.05)
4. **Test statistic:** A number computed from data that measures evidence against H₀
5. **p-value:** Probability of observing results at least this extreme if H₀ is true
6. **Decision:** Reject H₀ if p < α

---

## Choosing the Right Test

| Question | Data type | Assumptions met? | Test |
|----------|-----------|-----------------|------|
| 1-sample mean vs. known value | Continuous | Normal / n>30 | 1-sample t-test |
| 2-sample means | Continuous | Normal, equal var | Independent t-test |
| 2-sample means, unequal var | Continuous | Normal | Welch's t-test |
| Paired measurements | Continuous | Normal differences | Paired t-test |
| 3+ group means | Continuous | Normal, equal var | One-way ANOVA |
| Observed vs. expected counts | Categorical | Expected ≥ 5 per cell | Chi-square goodness-of-fit |
| Association between 2 categorical | Categorical | Expected ≥ 5 per cell | Chi-square independence |
| 2×2 table with small counts | Categorical | Any | Fisher's exact test |
| Non-normal 2-sample | Ordinal/continuous | — | Mann-Whitney U |

---

## Chi-Square Goodness-of-Fit

Tests whether observed frequencies match a hypothesised distribution.

**H₀:** Observed distribution matches expected  
**H₁:** Observed distribution differs from expected

```python
import numpy as np
from scipy import stats
import matplotlib.pyplot as plt

def chi_square_gof(observed, expected, alpha=0.05):
    """
    Chi-square goodness-of-fit test.
    observed, expected: array-like of counts (same length)
    Returns: chi2 statistic, p-value, degrees of freedom
    """
    obs = np.array(observed, dtype=float)
    exp = np.array(expected, dtype=float)

    # Critical assumption check: all expected counts >= 5
    if np.any(exp < 5):
        raise ValueError(
            f"Expected counts < 5 found: {exp[exp < 5]}. "
            "Pool categories or use Fisher's exact test."
        )

    chi2_stat, p_value = stats.chisquare(f_obs=obs, f_exp=exp)
    dof = len(obs) - 1
    return chi2_stat, p_value, dof

# Example: fair die
observed = [18, 22, 17, 25, 19, 19]   # 120 rolls
expected = [20, 20, 20, 20, 20, 20]   # uniform

chi2, p, dof = chi_square_gof(observed, expected)
print(f"χ²({dof}) = {chi2:.3f}, p = {p:.4f}")
print("Decision:", "Reject H₀" if p < 0.05 else "Fail to reject H₀")

# Visualize
categories = [f"Face {i+1}" for i in range(6)]
x = np.arange(len(categories))
fig, ax = plt.subplots(figsize=(8, 4))
ax.bar(x - 0.2, observed, 0.4, label="Observed", color="orange")
ax.bar(x + 0.2, expected, 0.4, label="Expected", color="steelblue", alpha=0.7)
ax.set_xticks(x); ax.set_xticklabels(categories)
ax.set_title(f"Goodness-of-Fit — χ²={chi2:.3f}, p={p:.4f}")
ax.legend()
plt.tight_layout()
plt.show()
```

### Effect Size: Cramér's V

```python
def cramers_v(chi2_stat, n, k):
    """Cramér's V for chi-square tests. k = min(rows, cols)."""
    return np.sqrt(chi2_stat / (n * (k - 1)))

# For GOF: k = number of categories
V = cramers_v(chi2, n=sum(observed), k=len(observed))
print(f"Cramér's V = {V:.3f}")  # 0.1–0.3 small, 0.3–0.5 medium, >0.5 large
```

---

## Chi-Square Test of Independence

Tests association between two categorical variables.

```python
def chi_square_independence(contingency_table, alpha=0.05):
    """
    contingency_table: 2D array-like (rows × cols)
    """
    ct = np.array(contingency_table)
    chi2, p, dof, expected = stats.chi2_contingency(ct, correction=False)

    if np.any(expected < 5):
        print("Warning: expected counts < 5 in some cells — use Fisher's exact test")

    n = ct.sum()
    k = min(ct.shape)
    V = cramers_v(chi2, n, k)

    return {
        "chi2": chi2, "p_value": p, "dof": dof,
        "cramers_v": V, "expected": expected
    }

# Example: treatment × outcome
table = [[45, 30], [25, 50]]  # [[treated-success, treated-fail], [control-success, control-fail]]
result = chi_square_independence(table)
print(f"χ²({result['dof']}) = {result['chi2']:.3f}, p = {result['p_value']:.4f}")
print(f"Cramér's V = {result['cramers_v']:.3f}")
```

---

## t-Tests

```python
# Independent samples (Welch's — handles unequal variances)
group_a = rng.normal(loc=5.2, scale=1.5, size=40)
group_b = rng.normal(loc=4.8, scale=1.8, size=35)

t_stat, p_value = stats.ttest_ind(group_a, group_b, equal_var=False)
print(f"Welch t({len(group_a)+len(group_b)-2}) = {t_stat:.3f}, p = {p_value:.4f}")

# Effect size: Cohen's d
def cohens_d(a, b):
    pooled_std = np.sqrt((np.var(a, ddof=1) + np.var(b, ddof=1)) / 2)
    return (np.mean(a) - np.mean(b)) / pooled_std

d = cohens_d(group_a, group_b)
print(f"Cohen's d = {d:.3f}")  # |d|: 0.2 small, 0.5 medium, 0.8 large
```

---

## Multiple Testing Correction

Running k tests at α=0.05 means ~5% false positives per test.

```python
from statsmodels.stats.multitest import multipletests

p_values = [0.01, 0.04, 0.08, 0.002, 0.06]

# Bonferroni (conservative)
reject_bonf, p_bonf, _, _ = multipletests(p_values, method="bonferroni", alpha=0.05)

# Benjamini-Hochberg FDR (less conservative, preferred)
reject_bh, p_bh, _, _ = multipletests(p_values, method="fdr_bh", alpha=0.05)

for i, (p, rb, rh) in enumerate(zip(p_values, reject_bonf, reject_bh)):
    print(f"Test {i+1}: p={p:.3f}  Bonferroni={rb}  BH-FDR={rh}")
```

---

## Confidence Intervals

```python
# Mean CI (t-based)
data = rng.normal(5, 2, 50)
n = len(data)
mean = np.mean(data)
se = stats.sem(data)
ci = stats.t.interval(confidence=0.95, df=n-1, loc=mean, scale=se)
print(f"Mean = {mean:.3f}, 95% CI = ({ci[0]:.3f}, {ci[1]:.3f})")

# Proportion CI (Wilson method — preferred over Wald)
from statsmodels.stats.proportion import proportion_confint
ci = proportion_confint(count=14, nobs=50, alpha=0.05, method="wilson")
print(f"Proportion 95% CI: ({ci[0]:.3f}, {ci[1]:.3f})")
```

---

## Common Mistakes

| Mistake | Problem | Correct Approach |
|---------|---------|-----------------|
| p < 0.05 = proven | p-value is not probability of H₀ being true | Report p with effect size and CI |
| p > 0.05 = no effect | Absence of evidence ≠ evidence of absence | Check statistical power (1-β) |
| Multiple t-tests instead of ANOVA | Inflated Type I error | Use one-way ANOVA then post-hoc |
| Ignoring assumption violations | Invalid p-values | Test assumptions; use non-parametric fallback |
| Expected counts < 5 in chi-square | Invalid test statistic | Pool cells or use Fisher's exact |
