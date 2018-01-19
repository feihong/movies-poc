(ns movies.showings
  (:require [clojure.string :as str]
            [movies.config :refer [env]]
            [movies.external.gracenote :as gracenote]
            [movies.external.omdb :as omdb]))


(defn get-theater-names [m]
  (->> (m :showtimes)
       (map #(-> % :theatre :name))
       set
       sort))

(defn gracenote->std [m]
  {:title (:title m)
   :year (:releaseYear m)
   :director (some->> m :directors (str/join ", "))
   :actors (some->> m :topCast (str/join ", "))
   :plot (:longDescription m)
   :url (:officialUrl m)
   :theaters (get-theater-names m)})

(defn get-additional-meta [m]
  (let [m2 (omdb/fetch-movie (:title m) (:year m))
        ; Take the plot that is longest
        plot1 (:plot m)
        plot2 (:plot m2)
        plot (if (> (count plot1) (count plot2)) plot1 plot2)]
    (-> (merge m m2)
        (assoc :plot plot))))

(defn movie-showings []
  (->> (gracenote/fetch-movie-showings)
       (filter #(% :releaseYear))
       (sort-by #(% :releaseDate) #(compare %2 %1))
       (map gracenote->std)
       (map get-additional-meta)))
