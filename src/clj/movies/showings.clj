(ns movies.showings
  (:require [movies.config :refer [env]]
            [movies.external.gracenote :as gracenote]))


(defn get-theater-names [m]
  (->> (m :showtimes)
       (map #(-> % :theatre :name))
       set
       sort))

(defn ensure-fields [m]
  "Ensure that topCast and directors fields are lists."
  (let [ensure-list (fnil identity [])]
    (-> m
        (update :topCast ensure-list)
        (update :directors ensure-list)
        (assoc :theaters (get-theater-names m)))))

(defn gracenote->std [m]
  {:title (:title m)
   :director (->> m :directors (str/join ", "))
   :actors (->> m :topCast (str/join ", "))
   :plot (:longDescription m)
   :year (:releaseYear m)
   :theaters (get-theater-names m)})

(defn movie-showings []
  (->> (gracenote/fetch-movie-showings)
       (filter #(% :releaseYear))
       (sort-by #(% :releaseDate) #(compare %2 %1))
       (map ensure-fields)))
