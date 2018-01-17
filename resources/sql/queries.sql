-- :name update-cache! :! :n
-- :doc Update the cache
MERGE INTO cache
(url, content, modified_at) KEY (url)
VALUES (:url, :content, :modified_at)

-- :name get-cache :? :1
-- :doc Retrieves content from cache
SELECT content, modified_at FROM cache
WHERE url = :url

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
(title, year, directed_by, cast, country, lang, summary)
VALUES (:title, :year, :directed_by, :cast, :country, :lang, :summary)
