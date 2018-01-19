(ns movies.showings
  (:require [clojure.string :as str]
            [movies.config :refer [env]]
            [movies.external.gracenote :as gracenote]))


(defn get-theater-names [m]
  (->> (m :showtimes)
       (map #(-> % :theatre :name))
       set
       sort))

(defn gracenote->std [m]
  {:title (:title m)
   :director (some->> m :directors (str/join ", "))
   :actors (some->> m :topCast (str/join ", "))
   :plot (:longDescription m)
   :year (:releaseYear m)
   :theaters (get-theater-names m)})

(defn movie-showings []
  (->> (gracenote/fetch-movie-showings)
       (filter #(% :releaseYear))
       (sort-by #(% :releaseDate) #(compare %2 %1))
       (map gracenote->std)))
