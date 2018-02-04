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

(defn get-runtime [m]
  (some->> (m :runTime)
           (re-matches #"PT(\d+)H(\d+)M")
           rest   ; only get two groups
           (map #(Integer/parseInt %))
           ((fn [[h m]] (str h "h " m "m")))))  ; note extra parens around fn

(defn gracenote->std [m]
  {:title (:title m)
   :year (:releaseYear m)
   :director (some->> m :directors (str/join ", "))
   :actors (some->> m :topCast (str/join ", "))
   :plot (:longDescription m)
   :url (:officialUrl m)
   ; :showtimes (get-showtimes m)
   :runtime (get-runtime m)})

(defn get-additional-meta [m]
  (let [m2 (omdb/fetch-movie (:title m) (:year m))
        ; Take the plot that is longest
        plot (max-key count (:plot m) (:plot m2))]
    (-> (merge m m2)
        (assoc :plot plot))))

(defn movie-showings []
  ; (->> (cache/without-caching (gracenote/fetch-movie-showings 3))
  (->> (gracenote/fetch-movie-showings 0)
       (filter :releaseYear)
       (map gracenote->std)
       (map get-additional-meta)
       criteria/check-criteria
       ; Make sure items that match criteria appear first
       (sort-by (juxt (complement :matches-criteria) :title))))
