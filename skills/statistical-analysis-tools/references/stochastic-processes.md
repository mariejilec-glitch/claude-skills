# Stochastic Processes

## Martingale Process

A Martingale is a stochastic process where the expected future value, given all past values, equals the current value. In other words: no systematic drift — it is a "fair game."

**Formal definition:** E[X_{n+1} | X_0, …, X_n] = X_n

### Simple Random Walk (symmetric Martingale)

```python
import numpy as np
import matplotlib.pyplot as plt

def simulate_random_walk(n_steps=200, n_paths=5, seed=42):
    """Simulate symmetric ±1 random walk paths."""
    rng = np.random.default_rng(seed)
    steps = rng.choice([-1, 1], size=(n_paths, n_steps))
    paths = np.cumsum(steps, axis=1)
    # Prepend starting value 0
    paths = np.hstack([np.zeros((n_paths, 1)), paths])
    return paths

paths = simulate_random_walk(n_steps=300, n_paths=8)

plt.figure(figsize=(12, 5))
for path in paths:
    plt.plot(path, alpha=0.7)
plt.axhline(0, color="black", linewidth=0.8, linestyle="--")
plt.title("Random Walk — 8 Paths")
plt.xlabel("Step"); plt.ylabel("Position")
plt.show()

# Key property: E[X_n] = 0, Var[X_n] = n (diffusive growth)
n = 300
print(f"Expected spread after {n} steps: ±{np.sqrt(n):.1f}")
```

### Martingale Betting Strategy

The classical doubling strategy: after each loss, double the bet.

```python
def simulate_martingale_betting(capital=100, rounds=50, seed=None):
    """
    Martingale betting strategy on a fair coin flip.
    Returns capital history across rounds.
    """
    rng = np.random.default_rng(seed)
    history = [capital]
    bet = 1

    for _ in range(rounds):
        win = rng.random() < 0.5
        if win:
            capital += bet
            bet = 1           # reset after win
        else:
            capital -= bet
            bet = min(bet * 2, capital)  # double, but can't bet more than held

        history.append(capital)
        if capital <= 0:
            break

    return history

# Run multiple simulations
fig, ax = plt.subplots(figsize=(12, 5))
for i in range(10):
    hist = simulate_martingale_betting(capital=200, rounds=100, seed=i)
    ax.plot(hist, alpha=0.6)
ax.axhline(200, color="black", linewidth=0.8, linestyle="--", label="Starting capital")
ax.set_title("Martingale Betting — 10 Simulations")
ax.set_xlabel("Round"); ax.set_ylabel("Capital")
ax.legend()
plt.show()
```

**Theoretical note:** The Martingale strategy has expected profit of 0 on a fair game. In practice it fails because (a) casinos impose bet limits, and (b) finite capital means ruin is inevitable with probability 1 over long sequences.

---

## Logistic Map & Chaos Theory

The logistic map is a discrete-time dynamical system that demonstrates how simple deterministic rules produce complex, chaotic behavior.

**Equation:** x_{n+1} = r · x_n · (1 − x_n)

- x ∈ (0, 1): population fraction
- r ∈ (0, 4]: growth rate parameter

### Behavior by r

| r range | Behavior |
|---------|----------|
| 0 < r < 1 | Population → 0 (extinction) |
| 1 ≤ r < 3 | Stable fixed point convergence |
| 3 ≤ r < 3.57 | Period doubling (2 → 4 → 8 …) |
| r ≈ 3.57 | Onset of chaos |
| 3.57 < r ≤ 4 | Chaotic — sensitive dependence on initial conditions |
| r = 4 | Fully chaotic |

```python
def logistic_map(r, x0, n_steps=100):
    """Iterate the logistic map for n_steps."""
    xs = [x0]
    x = x0
    for _ in range(n_steps):
        x = r * x * (1 - x)
        xs.append(x)
    return np.array(xs)

# Compare stable vs chaotic regime
fig, axes = plt.subplots(2, 2, figsize=(14, 8))

configs = [(2.8, "Stable fixed point"), (3.2, "Period-2 cycle"),
           (3.56, "Period-8 / near chaos"), (3.9, "Chaos")]

for ax, (r, title) in zip(axes.flat, configs):
    xs = logistic_map(r, x0=0.4, n_steps=80)
    ax.plot(xs, "o-", markersize=3, linewidth=0.8)
    ax.set_title(f"r={r} — {title}")
    ax.set_xlabel("Step"); ax.set_ylabel("x")
    ax.set_ylim(0, 1)

plt.tight_layout()
plt.show()
```

### Sensitivity to Initial Conditions

Hallmark of chaos: infinitesimally different initial conditions diverge exponentially.

```python
r = 3.9
n = 80

x1 = logistic_map(r, x0=0.400000, n_steps=n)
x2 = logistic_map(r, x0=0.400001, n_steps=n)  # ε = 1e-6 difference

plt.figure(figsize=(12, 4))
plt.plot(x1, label="x₀ = 0.400000")
plt.plot(x2, label="x₀ = 0.400001", linestyle="--")
plt.title(f"Sensitive Dependence on Initial Conditions (r={r})")
plt.xlabel("Step"); plt.ylabel("x"); plt.legend()
plt.show()

divergence = np.abs(x1 - x2)
plt.figure(figsize=(12, 4))
plt.semilogy(divergence)
plt.title("Log-scale divergence between two trajectories")
plt.xlabel("Step"); plt.ylabel("|x₁ - x₂|")
plt.show()
```

### Bifurcation Diagram

Shows all long-run behaviors as r varies.

```python
def bifurcation_diagram(r_min=2.5, r_max=4.0, n_r=2000, n_warmup=300, n_collect=100):
    rs, xs = [], []
    for r in np.linspace(r_min, r_max, n_r):
        x = 0.5
        for _ in range(n_warmup):     # discard transient
            x = r * x * (1 - x)
        for _ in range(n_collect):    # collect attractor
            x = r * x * (1 - x)
            rs.append(r)
            xs.append(x)
    return rs, xs

rs, xs = bifurcation_diagram()

plt.figure(figsize=(14, 6))
plt.scatter(rs, xs, s=0.1, color="black", alpha=0.4)
plt.title("Logistic Map — Bifurcation Diagram")
plt.xlabel("r"); plt.ylabel("x (long-run values)")
plt.show()
```

### Lyapunov Exponent

Quantifies the rate of divergence. λ > 0 → chaotic; λ < 0 → stable; λ = 0 → bifurcation point.

```python
def lyapunov_exponent(r, x0=0.5, n=1000):
    x = x0
    le = 0.0
    for _ in range(n):
        x = r * x * (1 - x)
        derivative = abs(r * (1 - 2 * x))
        if derivative == 0:
            return -np.inf
        le += np.log(derivative)
    return le / n

rs_range = np.linspace(2.5, 4.0, 500)
les = [lyapunov_exponent(r) for r in rs_range]

plt.figure(figsize=(12, 4))
plt.plot(rs_range, les, linewidth=0.8)
plt.axhline(0, color="red", linewidth=0.8, linestyle="--", label="λ=0 (bifurcation)")
plt.title("Lyapunov Exponent vs r")
plt.xlabel("r"); plt.ylabel("λ"); plt.legend()
plt.show()
```

---

## Geometric Brownian Motion (GBM)

Used to model asset prices and continuous-time random processes.

```python
def geometric_brownian_motion(S0, mu, sigma, T, dt, seed=42):
    """
    S0: initial value, mu: drift, sigma: volatility,
    T: total time, dt: time step
    """
    rng = np.random.default_rng(seed)
    n = int(T / dt)
    t = np.linspace(0, T, n)
    W = rng.standard_normal(n)
    W = np.cumsum(W) * np.sqrt(dt)  # Brownian motion
    S = S0 * np.exp((mu - 0.5 * sigma**2) * t + sigma * W)
    return t, S

t, S = geometric_brownian_motion(S0=100, mu=0.05, sigma=0.2, T=1, dt=1/252)
plt.figure(figsize=(12, 4))
plt.plot(t, S)
plt.title("Geometric Brownian Motion")
plt.xlabel("Time (years)"); plt.ylabel("Value")
plt.show()
```

---

## When to Use Each Process

| Process | When to use |
|---------|------------|
| Random walk | Discrete steps, fair game, Martingale testing |
| Logistic map | Population dynamics, chaos demonstration, discrete nonlinear systems |
| GBM | Continuous-time asset prices, diffusion processes |
| Martingale betting | Risk analysis, gambler's ruin, strategy evaluation |
