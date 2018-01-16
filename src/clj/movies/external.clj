(ns movies.external
  (:require [movies.config :refer [env]]
            [org.httpkit.client :as http]
            [clj-time.core]
            [clj-time.format :as f]
            [clojure.data.json :as json]))


; Coordinates of AMC River East 21.
(def coordinates [41.891377 -87.618997])


(defn movie-showings []
  ; Docs: http://developer.tmsapi.com/docs/read/data_v1_1/movies/Movies_playing_in_local_theatres
  (let [url "http://data.tmsapi.com/v1.1/movies/showings"
        date-str (-> (f/formatters :date)
                     (f/unparse (clj-time.core/now)))
        qp {:startDate date-str
            :api_key (-> env :gracenote :api-key)
            :lat (first coordinates)
            :lng (second coordinates)}]
    (-> @(http/get url {:query-params qp})
        :body
        json/read-str)))


(defn movie-meta [title year]
  (let [url "http://omdbapi.com/"
        qp {:t title
            :y year
            :type "movie"
            :apikey (-> env :omdb :api-key)}]
    (-> @(http/get url {:query-params qp})
        :body
        json/read-str)))
