(ns movies.external.omdb
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [org.httpkit.client :as http]
            [movies.config :refer [env]]
            [movies.cache :as cache]))


(defn fetch-movie-from-api [title year]
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
        json/parse-string true)))

(def key-mapping-pairs
  (->> [:title, :year, :director, :actors, :country, :language, :plot, :poster]
       (map (fn [x] [(-> x name str/capitalize keyword), x]))
       (apply list)))

(defn omdb->std [src]
  (as-> key-mapping-pairs $
        (reduce (fn [acc [k1 k2]] (assoc acc k2 (src k1))) {} $)
        (update $ :year #(Integer/parseInt %))))

; (defn  [title year]
;   (let [movie (db/get-movie {:title title :year year})]
;     (if movie
;       movie
;       (let [data (fetch-movie-meta title year)
;             new-movie (omdb->ours data)]
;         (db/create-movie! new-movie)
;         new-movie))))
