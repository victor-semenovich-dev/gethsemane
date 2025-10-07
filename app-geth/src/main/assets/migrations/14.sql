ALTER TABLE Sermons ADD COLUMN show_in_list INTEGER;
ALTER TABLE Sermons ADD COLUMN author TEXT;
ALTER TABLE Witnesses ADD COLUMN show_in_list INTEGER;
ALTER TABLE Witnesses ADD COLUMN author TEXT;
DELETE FROM Sermons WHERE worship_id = 0 AND audio_local_uri IS NULL;
DELETE FROM Witnesses WHERE worship_id = 0 AND audio_local_uri IS NULL;
