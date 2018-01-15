(ns movies.util
  (:require [clojure.data.json :as json]
            [cheshire.core]
            [movies.external :as external]))


(defn write-showings-to-file []
  (as-> (external/movie-showings) $
    (cheshire.core/generate-string $ {:pretty true})
    (spit "showings.json" $)))

(defn ensure-top-cast [m]
  (update m "topCast" (fnil identity [])))

(defn read-showings-from-file []
  (->> (slurp "showings.json")
       (json/read-str)
       (map ensure-top-cast)))
