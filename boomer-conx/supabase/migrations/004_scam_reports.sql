-- Global scam patterns (admin-maintained)
CREATE TABLE IF NOT EXISTS scam_patterns_global (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pattern TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('SMS', 'URL', 'CALLER', 'EMAIL')),
    severity INT DEFAULT 5 CHECK (severity BETWEEN 1 AND 10),
    description_fr TEXT NOT NULL,
    description_en TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS (readable by all authenticated users)
ALTER TABLE scam_patterns_global ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Authenticated users can read patterns"
    ON scam_patterns_global FOR SELECT
    TO authenticated
    USING (active = true);

-- Community scam reports
CREATE TABLE IF NOT EXISTS scam_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_id UUID REFERENCES profiles(id) ON DELETE SET NULL,
    phone_number TEXT,
    sms_content TEXT,
    url TEXT,
    report_type TEXT NOT NULL CHECK (report_type IN ('SMS', 'URL', 'CALLER', 'EMAIL')),
    status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'verified', 'dismissed')),
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS
ALTER TABLE scam_reports ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can submit reports"
    ON scam_reports FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid() = reporter_id);

CREATE POLICY "Users can view own reports"
    ON scam_reports FOR SELECT
    USING (auth.uid() = reporter_id);

-- Indexes
CREATE INDEX idx_scam_patterns_type ON scam_patterns_global(type) WHERE active = true;
CREATE INDEX idx_scam_reports_type ON scam_reports(report_type);

-- Seed initial Quebec-specific scam patterns
INSERT INTO scam_patterns_global (pattern, type, severity, description_fr, description_en) VALUES
('vous avez gagn[eé]', 'SMS', 8, 'Message de loterie frauduleux', 'Fraudulent lottery message'),
('cliquez ici pour confirmer', 'SMS', 7, 'Tentative d''hameçonnage par SMS', 'SMS phishing attempt'),
('revenu qu[eé]bec.*remboursement', 'SMS', 9, 'Faux message de Revenu Québec', 'Fake Revenu Quebec message'),
('canada revenue.*refund', 'SMS', 9, 'Faux message de l''ARC', 'Fake CRA message'),
('votre compte.*bloqu[eé]', 'SMS', 8, 'Fausse alerte de compte bloqué', 'Fake blocked account alert'),
('desjardins.*urgent', 'SMS', 8, 'Faux message Desjardins', 'Fake Desjardins message'),
('banque nationale.*v[eé]rif', 'SMS', 8, 'Faux message Banque Nationale', 'Fake Banque Nationale message'),
('livraison.*frais', 'SMS', 6, 'Arnaque de frais de livraison', 'Delivery fee scam'),
('postes canada.*colis', 'SMS', 7, 'Faux message Postes Canada', 'Fake Canada Post message'),
('bit\.ly|tinyurl|t\.co', 'URL', 5, 'Lien raccourci suspect', 'Suspicious shortened link')
ON CONFLICT DO NOTHING;
