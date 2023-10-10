DROP TABLE IF EXISTS users, categories;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  email VARCHAR(254) NOT NULL,
  name VARCHAR(250) NOT NULL,
  CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  lat REAL NOT NULL,
  lon REAL NOT NULL,
  CONSTRAINT uq_lat_lon UNIQUE (lat, lon)
);

CREATE TABLE IF NOT EXISTS events (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  title VARCHAR(120) NOT NULL,
  annotation VARCHAR(2000) NOT NULL,
  category_id INTEGER NOT NULL,
  confirmed_requests INTEGER,
  created_on TIMESTAMP,
  description VARCHAR(7000),
  event_date TIMESTAMP NOT NULL,
  initiator_id BIGINT NOT NULL,
  location_id INTEGER NOT NULL,
  paid BOOLEAN NOT NULL,
  participant_limit INTEGER,
  published_on TIMESTAMP,
  request_moderation BOOLEAN,
  state VARCHAR(25) NOT NULL,
  FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE RESTRICT,
  FOREIGN KEY(initiator_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY(location_id) REFERENCES locations(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS participation_requests (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  created TIMESTAMP NOT NULL,
  event_id INTEGER NOT NULL,
  requester_id BIGINT NOT NULL,
  status VARCHAR(25) NOT NULL,
  FOREIGN KEY(event_id) REFERENCES events(id) ON DELETE CASCADE,
  FOREIGN KEY(requester_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    title VARCHAR(50) NOT NULL,
    pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);