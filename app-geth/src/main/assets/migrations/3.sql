DROP TABLE IF EXISTS Sermons;
DROP TABLE IF EXISTS Sermons2;
CREATE TABLE Sermons (_id INTEGER PRIMARY KEY, external_id INTEGER, title TEXT, author_id INTEGER, content TEXT, date INTEGER, audio_uri TEXT, worship_id INTEGER);
