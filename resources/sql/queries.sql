-- :name update-cache! :! :n
-- :doc Update the cache
INSERT INTO cache
(key, content, expires_at)
VALUES (:key, :content, :expires_at)
ON CONFLICT (key)
DO UPDATE SET
  content = EXCLUDED.content,
  expires_at = EXCLUDED.expires_at

-- :name get-cache :? :1
-- :doc Retrieves content from cache
SELECT content FROM cache
WHERE key = :key AND
      expires_at > current_timestamp

-- :name get-movies :? :n
-- :doc Retrieves all movies
SELECT *
FROM movies

-- :name get-movie :? :1
-- :doc Retrieves a movie
SELECT *
FROM movies
WHERE title = :title AND year = :year

-- :name create-movie! :! :n
-- :doc Creates a new movie record
INSERT INTO movies
(title, year, director, actors, country, language, plot, poster)
VALUES (:title, :year, :director, :actors, :country, :language, :plot, :poster)
