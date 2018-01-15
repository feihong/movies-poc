(ns movies.util
  (:require [clojure.data.json :as json]
            [cheshire.core]
            [movies.external :as external]))


(defn write-showtimes-to-file []
  (as-> (external/movie-showtimes) $
    (cheshire.core/generate-string $ {:pretty true})
    (spit "showtimes.json" $)))

(defn ensure-top-cast [m]
  (update m "topCast" (fnil identity [])))

(defn read-showtimes-from-file []
  (->> (slurp "showtimes.json")
       (json/read-str)
       (map ensure-top-cast)))
