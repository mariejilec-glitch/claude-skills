# Entropy & Information Theory

## Overview

Shannon entropy quantifies the average uncertainty or information content in a random variable's outcomes. High entropy means outcomes are unpredictable (uniform distribution); low entropy means one or few outcomes dominate.

**Formula:** H(X) = -Σ p(x) · log₂(p(x))

Units: bits (log₂), nats (ln), or hartleys (log₁₀).

---

## Python Implementation

```python
import numpy as np
from scipy.stats import entropy as scipy_entropy
from collections import Counter

def shannon_entropy(sequence, base=2):
    """
    Compute Shannon entropy of a discrete sequence.
    Returns entropy in bits (base=2) or nats (base=e).
    """
    counts = Counter(sequence)
    total = len(sequence)
    probabilities = np.array([c / total for c in counts.values()])
    return -np.sum(probabilities * np.log(probabilities) / np.log(base))

# scipy equivalent (accepts probability array)
def entropy_from_counts(counts_array, base=2):
    pk = np.array(counts_array) / np.sum(counts_array)
    return scipy_entropy(pk, base=base)

# Example
draws = ["A", "B", "A", "C", "A", "B", "D"]
H = shannon_entropy(draws)
print(f"Entropy: {H:.3f} bits")   # e.g. 1.842 bits
```

### Maximum Entropy
For n equally likely outcomes: H_max = log₂(n)

```python
n = 6  # fair die
H_max = np.log2(n)  # 2.585 bits
```

---

## Interpreting Entropy Values

| Entropy (bits) | Interpretation | Example |
|----------------|----------------|---------|
| 0 | Completely predictable | All draws are "A" |
| 0–1 | Low uncertainty | One outcome strongly dominates |
| log₂(n)/2 | Moderate uncertainty | A few outcomes split probability |
| log₂(n) | Maximum / uniform | All outcomes equally likely |

---

## Frequency Analysis & Cryptanalysis

Entropy is the core metric in frequency analysis — detecting non-randomness in ciphertext or encoded sequences.

```python
def frequency_analysis(text):
    """Return sorted character frequencies and entropy."""
    text = text.lower().replace(" ", "")
    counts = Counter(text)
    total = len(text)
    freq = {ch: cnt / total for ch, cnt in counts.items()}
    H = shannon_entropy(list(text))
    return sorted(freq.items(), key=lambda x: -x[1]), H

# Low entropy → structured / not random → easier to crack
# High entropy → near-uniform → harder to crack
```

**English text** has roughly 4.0–4.5 bits/character due to letter frequency patterns.  
**Random bytes** have entropy close to 8 bits/byte (log₂(256)).

---

## Conditional Entropy & Mutual Information

```python
def joint_entropy(x_seq, y_seq, base=2):
    pairs = list(zip(x_seq, y_seq))
    return shannon_entropy(pairs, base=base)

def mutual_information(x_seq, y_seq, base=2):
    H_x = shannon_entropy(x_seq, base=base)
    H_y = shannon_entropy(y_seq, base=base)
    H_xy = joint_entropy(x_seq, y_seq, base=base)
    return H_x + H_y - H_xy
```

---

## Cross-Entropy & KL Divergence

```python
from scipy.stats import entropy as kl_div

# KL divergence: how different is Q from true distribution P?
def kl_divergence(p_counts, q_counts, base=2):
    p = np.array(p_counts, dtype=float)
    q = np.array(q_counts, dtype=float)
    p /= p.sum()
    q /= q.sum()
    # scipy entropy(pk, qk) = KL(pk || qk)
    return scipy_entropy(p, q, base=base)
```

---

## Visualization

```python
import matplotlib.pyplot as plt

def plot_frequency_distribution(sequence, title="Frequency Distribution"):
    counts = Counter(sequence)
    labels = sorted(counts.keys())
    values = [counts[k] for k in labels]
    H = shannon_entropy(sequence)

    fig, ax = plt.subplots()
    ax.bar(labels, values, color="steelblue")
    ax.set_title(f"{title} — H = {H:.3f} bits")
    ax.set_xlabel("Symbol")
    ax.set_ylabel("Count")
    plt.tight_layout()
    plt.show()
```

---

## When to Use Entropy

| Use case | Notes |
|----------|-------|
| Randomness testing | Compare H to H_max; low ratio suggests structure |
| Feature selection (decision trees) | Information gain = H(parent) - H(children) |
| Compression quality | Near-H_max sequences compress poorly |
| Anomaly detection | Sudden entropy drop → data became structured |
| Cryptanalysis | Near-H_max → good encryption; low H → vulnerable |

## When NOT to Use Entropy

- On continuous variables without binning (use differential entropy instead)
- As a standalone significance test (pair with chi-square for formal testing)
- With very small samples (<30 observations) — estimates are unreliable
