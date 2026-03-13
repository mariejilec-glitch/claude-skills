import { serve } from "https://deno.land/std@0.177.0/http/server.ts";

interface CheckUrlRequest {
  url: string;
}

interface CheckUrlResponse {
  safe: boolean;
  reason: string | null;
  severity: number;
}

// Known phishing patterns
const SUSPICIOUS_PATTERNS = [
  { pattern: /bit\.ly|tinyurl|t\.co/i, reason: "Lien raccourci suspect", severity: 4 },
  { pattern: /desjardins.*login/i, reason: "Possible hameçonnage Desjardins", severity: 9 },
  { pattern: /revenu.*quebec.*login/i, reason: "Possible hameçonnage Revenu Québec", severity: 9 },
  { pattern: /canada.*revenue.*login/i, reason: "Possible hameçonnage ARC", severity: 9 },
  { pattern: /paypal.*verify/i, reason: "Possible hameçonnage PayPal", severity: 8 },
  { pattern: /\.ru\/|\.cn\/|\.tk\//i, reason: "Domaine suspect", severity: 6 },
];

serve(async (req: Request) => {
  if (req.method !== "POST") {
    return new Response(JSON.stringify({ error: "Method not allowed" }), {
      status: 405,
      headers: { "Content-Type": "application/json" },
    });
  }

  try {
    const { url }: CheckUrlRequest = await req.json();

    if (!url) {
      return new Response(JSON.stringify({ error: "URL is required" }), {
        status: 400,
        headers: { "Content-Type": "application/json" },
      });
    }

    // Check against local patterns
    for (const { pattern, reason, severity } of SUSPICIOUS_PATTERNS) {
      if (pattern.test(url)) {
        const response: CheckUrlResponse = { safe: false, reason, severity };
        return new Response(JSON.stringify(response), {
          headers: { "Content-Type": "application/json" },
        });
      }
    }

    // URL appears safe
    const response: CheckUrlResponse = { safe: true, reason: null, severity: 0 };
    return new Response(JSON.stringify(response), {
      headers: { "Content-Type": "application/json" },
    });
  } catch {
    return new Response(JSON.stringify({ error: "Invalid request" }), {
      status: 400,
      headers: { "Content-Type": "application/json" },
    });
  }
});
