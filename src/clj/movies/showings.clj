(ns movies.showings
  (:require [clojure.string :as str]
            [clj-time.format :as f]
            [movies.config :refer [env]]
            [movies.external.gracenote :as gracenote]
            [movies.external.omdb :as omdb]
            [movies.criteria :as criteria]
            [movies.cache :as cache]))


(defn get-datetimes [showtimes]
  "Convert showtimes from maps to datetime objects"
  (->> showtimes
       (map #(->> %
                  :dateTime
                  (f/parse (f/formatters :date-hour-minute))))))

(defn get-showtimes [m]
  "Get showtimes grouped by theater name"
  (->> (m :showtimes)
       (group-by #(-> % :theatre :name))
       (map (fn [[k v]] [k (get-datetimes v)]))))

(defn gracenote->std [m]
  {:title (:title m)
   :year (:releaseYear m)
   :director (some->> m :directors (str/join ", "))
   :actors (some->> m :topCast (str/join ", "))
   :plot (:longDescription m)
   :url (:officialUrl m)
   :showtimes (get-showtimes m)})

(defn get-additional-meta [m]
  (let [m2 (omdb/fetch-movie (:title m) (:year m))
        ; Take the plot that is longest
        plot (max-key count (:plot m) (:plot m2))]
    (-> (merge m m2)
        (assoc :plot plot))))

(defn movie-showings []
  ; (->> (cache/without-caching (gracenote/fetch-movie-showings 5)))
  (->> (gracenote/fetch-movie-showings 4)
       (filter :releaseYear)
       (map gracenote->std)
       (map get-additional-meta)
       criteria/assign-scores
       ; Sort by score descending and title ascending
       (sort-by (fn [x] [(-> x :criteria-score -), (:title x)]))))
