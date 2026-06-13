# Probability Distributions

## Quick Selection Guide

| Data type | Range | Distribution | Use case |
|-----------|-------|--------------|----------|
| Count of events in interval | 0, 1, 2, … | Poisson | Calls/hour, errors/page |
| Binary success/failure | 0 to n | Binomial | A/B tests, defect rates |
| Continuous, symmetric | (-∞, ∞) | Normal | Measurement error, CLT results |
| Time between events | (0, ∞) | Exponential | Wait times, failure times |
| Extreme values / long tail | (0, ∞) | Log-Normal | Income, reaction times |
| Success on k-th trial | 1, 2, 3, … | Geometric | Retries until first success |

---

## Poisson Distribution

Models the number of independent events occurring in a fixed interval when the average rate λ is known.

**Assumptions:**
- Events occur independently
- Average rate λ is constant
- Two events cannot happen at the exact same instant

```python
import numpy as np
from scipy import stats
import matplotlib.pyplot as plt

lam = 3.5  # average events per interval

dist = stats.poisson(mu=lam)

# PMF, CDF
k = np.arange(0, 15)
pmf = dist.pmf(k)
cdf = dist.cdf(k)

print(f"P(X=0) = {dist.pmf(0):.4f}")
print(f"P(X<=5) = {dist.cdf(5):.4f}")
print(f"Mean = {dist.mean()}, Var = {dist.var()}")

# Simulate
rng = np.random.default_rng(seed=42)
samples = rng.poisson(lam=lam, size=1000)

# Visualize
fig, axes = plt.subplots(1, 2, figsize=(12, 4))
axes[0].bar(k, pmf, color="steelblue", alpha=0.8)
axes[0].set_title(f"Poisson PMF (λ={lam})")
axes[0].set_xlabel("k"); axes[0].set_ylabel("P(X=k)")

axes[1].hist(samples, bins=range(0, 15), density=True, color="orange", alpha=0.7)
axes[1].plot(k, pmf, "ko-", label="Theoretical")
axes[1].set_title("Simulated vs Theoretical")
axes[1].legend()
plt.tight_layout()
plt.show()
```

### Poisson Approximation to Binomial
When n is large and p is small: Poisson(λ = np) ≈ Binomial(n, p)

```python
n, p = 1000, 0.003
lam_approx = n * p  # 3.0
# Poisson(3.0) approximates Binomial(1000, 0.003)
```

---

## Normal Distribution

```python
mu, sigma = 0, 1
dist = stats.norm(loc=mu, scale=sigma)

# Key quantiles
print(f"95th percentile: {dist.ppf(0.95):.3f}")
print(f"P(-1 < X < 1)  = {dist.cdf(1) - dist.cdf(-1):.4f}")  # ~0.6827
print(f"P(-2 < X < 2)  = {dist.cdf(2) - dist.cdf(-2):.4f}")  # ~0.9545

# Test normality
data = rng.normal(loc=5, scale=2, size=200)
stat, p_value = stats.shapiro(data)
print(f"Shapiro-Wilk: W={stat:.4f}, p={p_value:.4f}")
# p > 0.05 → fail to reject normality
```

---

## Binomial Distribution

```python
n_trials, p_success = 20, 0.3
dist = stats.binom(n=n_trials, p=p_success)

print(f"P(X=6) = {dist.pmf(6):.4f}")
print(f"Mean = {dist.mean():.1f}, Std = {dist.std():.3f}")

# Confidence interval for proportion
successes = 7
ci_low, ci_high = stats.proportion_confint(successes, n_trials, alpha=0.05, method="wilson")
print(f"95% CI for p: ({ci_low:.3f}, {ci_high:.3f})")
```

---

## Exponential Distribution

```python
rate = 0.5  # events per unit time; mean = 1/rate = 2
dist = stats.expon(scale=1/rate)

# Memoryless property: P(X > s+t | X > s) = P(X > t)
print(f"P(X > 3) = {1 - dist.cdf(3):.4f}")
print(f"Mean = {dist.mean():.1f}")

samples = rng.exponential(scale=1/rate, size=500)
```

---

## Distribution Fitting

```python
def fit_distribution(data, candidate_dists=None):
    """
    Fit multiple distributions to data and rank by KS test statistic.
    Lower KS statistic = better fit.
    """
    if candidate_dists is None:
        candidate_dists = [stats.norm, stats.expon, stats.lognorm, stats.gamma]

    results = []
    for dist in candidate_dists:
        params = dist.fit(data)
        ks_stat, p_val = stats.kstest(data, dist.name, args=params)
        results.append((dist.name, ks_stat, p_val, params))

    results.sort(key=lambda x: x[1])
    return results

# Usage
data = rng.gamma(a=2, scale=3, size=500)
fits = fit_distribution(data)
for name, ks, p, params in fits[:3]:
    print(f"{name:12s}  KS={ks:.4f}  p={p:.4f}")
```

---

## Q-Q Plot for Distribution Assessment

```python
def qq_plot(data, dist=stats.norm, title="Q-Q Plot"):
    fig, ax = plt.subplots(figsize=(6, 6))
    stats.probplot(data, dist=dist, plot=ax)
    ax.set_title(title)
    plt.tight_layout()
    plt.show()

# Points on the diagonal line → good fit
```

---

## Common Pitfalls

| Mistake | Consequence | Fix |
|---------|-------------|-----|
| Using Normal for count data | Invalid CI, wrong test | Use Poisson or Negative Binomial |
| Assuming Poisson when events aren't independent | Overdispersion | Check var > mean; use Negative Binomial |
| Small expected counts in Poisson cells | Chi-square invalid | Pool categories or use Fisher's exact test |
| Not testing fit assumptions | Wrong conclusions | Always run KS test or visual Q-Q check |
