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
  (->> [:title, :year, :director, :actors, :country, :language, :plot, :poster]
       (map (fn [x] [(-> x name str/capitalize keyword), x]))
       (apply list)))

(defn omdb->std [src]
  (as-> key-mapping-pairs $
        (reduce (fn [acc [k1 k2]] (assoc acc k2 (src k1))) {} $)))
        ; (update $ :year #(Integer/parseInt %))))

(defn fetch-movie [title year]
  "Fetch movie metadata, converted to standard format"
  (-> (fetch-movie-from-api title year)
      omdb->std))
