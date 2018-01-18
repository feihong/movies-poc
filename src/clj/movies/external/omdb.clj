(ns movies.external.omdb
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [org.httpkit.client :as http]
            [movies.config :refer [env]]
            [movies.db.core :as db]))


(def key-mapping-pairs
  (->> [:title, :year, :director, :actors, :country, :language, :plot, :poster]
       (map (fn [x] [(-> x name str/capitalize), x]))
       (apply list)))

(defn omdb->ours [src]
  (loop [pairs key-mapping-pairs
         target {}]
    (if (empty? pairs)
      ; Convert :year from string to int
      (update target :year #(Integer/parseInt %))
      (let [[src-key target-key] (peek pairs)]
        (recur (pop pairs) (assoc target target-key (src src-key)))))))

(defn fetch-movie-meta [title year]
  "Fetch movie metadata from OMDB API.

  Docs: http://www.omdbapi.com/
  "
  (let [url "http://omdbapi.com/"
        qp {:t title
            :y year
            :type "movie"
            :plot "full"
            :apikey (-> env :omdb :api-key)}]
    (-> @(http/get url {:query-params qp})
        :body
        json/read-str)))

(defn movie-meta [title year]
  (let [movie (db/get-movie {:title title :year year})]
    (if movie
      movie
      (let [data (fetch-movie-meta title year)
            new-movie (omdb->ours data)]
        (db/create-movie! new-movie)
        new-movie))))
