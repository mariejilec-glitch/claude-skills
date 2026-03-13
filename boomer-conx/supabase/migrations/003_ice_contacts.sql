-- ICE contacts cloud sync table
CREATE TABLE IF NOT EXISTS ice_contacts_cloud (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    relationship TEXT NOT NULL,
    priority INT DEFAULT 1,
    medical_notes TEXT,
    blood_type TEXT,
    medication_list TEXT,
    health_card_number TEXT,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS
ALTER TABLE ice_contacts_cloud ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can manage own ICE contacts"
    ON ice_contacts_cloud FOR ALL
    USING (auth.uid() = user_id);

-- Index
CREATE INDEX idx_ice_contacts_user
    ON ice_contacts_cloud(user_id);
