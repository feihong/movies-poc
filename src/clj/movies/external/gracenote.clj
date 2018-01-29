(ns movies.external.gracenote
  (:require [clojure.string :as str]
            [org.httpkit.client :as http]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.data.json :as json]
            [movies.config :refer [env]]
            [movies.cache :as cache]))


; Coordinates of AMC River East 21.
(def coordinates [41.891377 -87.618997])
(def api-url "http://data.tmsapi.com/v1.1/movies/showings")

(defn date-str [days-ahead]
  (let [fmt (f/formatters :date)
        dt (t/plus (t/now) (t/days days-ahead))]
    (f/unparse fmt dt)))

(defn fetch-movie-showings [days-ahead]
  "Fetch movie showings from Gracenote API
  Docs: http://developer.tmsapi.com/docs/read/data_v1_1/movies/Movies_playing_in_local_theatres
  "
  (let [qp {:startDate (date-str days-ahead)
            :numDays 2
            :api_key (-> env :gracenote :api-key)
            :lat (first coordinates)
            :lng (second coordinates)}]
     (cache/fetch-json "gracenote.showings"
                       api-url
                       {:query-params qp})))
