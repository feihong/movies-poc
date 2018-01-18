(ns movies.external.core
  (:require [org.httpkit.client :as http]
            [clj-time.core]
            [clj-time.format :as f]
            [clojure.data.json :as json]
            [movies.config :refer [env]]
            [movies.cache :as cache]
            [movies.db.core :as db]))


; (defn fetch-movie-meta [title year]
;   "Fetch movie metadata from OMDB API.
;
;   Docs: http://www.omdbapi.com/
;   "
;   (let [url "http://omdbapi.com/"
;         qp {:t title
;             :y year
;             :type "movie"
;             :plot "full"
;             :apikey (-> env :omdb :api-key)}]
;     (-> @(http/get url {:query-params qp})
;         :body
;         json/read-str)))
;
;
; (defn movie-meta [title year]
;   (let [movie (db/get-movie {:title title :year year})]
;     (if movie
;       movie
;       (let [result (fetch-movie-meta title year)
;             new-movie {}]))))


; Coordinates of AMC River East 21.
(def coordinates [41.891377 -87.618997])

(defn fetch-movie-showings []
  "Fetch movie showings from Gracenote API
  Docs: http://developer.tmsapi.com/docs/read/data_v1_1/movies/Movies_playing_in_local_theatres
  "
  (let [url "http://data.tmsapi.com/v1.1/movies/showings"
        date-str (-> (f/formatters :date)
                     (f/unparse (clj-time.core/now)))
        qp {:startDate date-str
            :api_key (-> env :gracenote :api-key)
            :lat (first coordinates)
            :lng (second coordinates)}]
    (-> (cache/fetch url {:query-params qp})
        json/read-str)))


(defn get-theater-names [m]
  (->> (m "showtimes")
       (map #(get-in % ["theatre" "name"]))
       set
       sort))

(defn ensure-fields [m]
  "Ensure that topCast and directors fields are lists."
  (let [ensure-list (fnil identity [])]
    (-> m
        (update "topCast" ensure-list)
        (update "directors" ensure-list)
        (assoc :theaters (get-theater-names m)))))


(defn movie-showings []
  (->> (fetch-movie-showings)
       (filter #(% "releaseYear"))
       (sort-by #(% "releaseDate") #(compare %2 %1))
       (map ensure-fields)))
