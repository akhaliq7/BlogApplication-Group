CREATE TABLE IF NOT EXISTS user (
  id int AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255),
  name VARCHAR(255),
  password VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS project (
  id int AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255),
  url VARCHAR(255),
  owner int,
  FOREIGN KEY (owner) REFERENCES user(id)

);

CREATE TABLE IF NOT EXISTS milestone (
  id int AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(255),
  intendedDueDate DATE,
  actualCompletionDate DATE,
  project int,
  FOREIGN KEY (project) REFERENCES project(id) ON DELETE CASCADE
);

-- DROP TABLE user;
-- DROP TABLE project;
-- DROP TABLE milestone;