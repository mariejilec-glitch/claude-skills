-- Create backup manifests table
CREATE TABLE IF NOT EXISTS backup_manifests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id) ON DELETE CASCADE NOT NULL,
    backup_type TEXT NOT NULL,
    item_count INT DEFAULT 0,
    size_bytes BIGINT DEFAULT 0,
    storage_path TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS
ALTER TABLE backup_manifests ENABLE ROW LEVEL SECURITY;

-- Users can only access their own backups
CREATE POLICY "Users can view own backups"
    ON backup_manifests FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own backups"
    ON backup_manifests FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own backups"
    ON backup_manifests FOR DELETE
    USING (auth.uid() = user_id);

-- Index for fast lookup by user and type
CREATE INDEX idx_backup_manifests_user_type
    ON backup_manifests(user_id, backup_type);

-- Create storage bucket for backups
INSERT INTO storage.buckets (id, name, public)
VALUES ('backups', 'backups', false)
ON CONFLICT DO NOTHING;

-- Storage policies
CREATE POLICY "Users can upload own backups"
    ON storage.objects FOR INSERT
    WITH CHECK (
        bucket_id = 'backups'
        AND (storage.foldername(name))[1] = auth.uid()::text
    );

CREATE POLICY "Users can read own backups"
    ON storage.objects FOR SELECT
    USING (
        bucket_id = 'backups'
        AND (storage.foldername(name))[1] = auth.uid()::text
    );

CREATE POLICY "Users can delete own backups"
    ON storage.objects FOR DELETE
    USING (
        bucket_id = 'backups'
        AND (storage.foldername(name))[1] = auth.uid()::text
    );
