(ns movies.external.omdb
  (:require [clojure.string :as str]
            [clj-time.core :as t]
            [movies.config :refer [env]]
            [movies.cache :as cache]))


(defn fetch-movie-from-api [title year]
  "Fetch movie metadata from OMDB API.
  http://www.omdbapi.com/

  If nothing is found, returns something like this:
  {:Response false :Error \"Movie not found!\"}
  "
  (let [url "http://omdbapi.com/"
        qp {:t title
            :y year
            :type "movie"
            :plot "full"
            :apikey (-> env :omdb :api-key)}]
    (cache/fetch-json [title year]
                      url
                      {:query-params qp}
                      :duration (t/months 3))))

(def key-mapping-pairs
  (let [capitalize-keyword #(-> % name str/capitalize keyword)]
    (->> [:director, :actors, :country, :language, :plot, :poster]
         (map (juxt capitalize-keyword identity)))))

(defn check-poster [m]
  "Delete the :poster value if it isn't a valid URL"
  (if (str/starts-with? (:poster m) "https://")
    m
    (dissoc m :poster)))

(defn omdb->std [src]
  (if (:Error src)
    {}
    (->> key-mapping-pairs
         ; Assoc :Director to :director, etc
         (reduce (fn [acc [k1 k2]] (assoc acc k2 (src k1))) {})
         check-poster)))

(defn fetch-movie [title year]
  "Fetch movie metadata, converted to standard format"
  (-> (fetch-movie-from-api title year)
      omdb->std))
