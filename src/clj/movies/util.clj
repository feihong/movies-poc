(ns movies.util
  (:require [clojure.data.json :as json]
            [cheshire.core]
            [movies.external :as external]))


(defn map->file [m path]
  (as-> m $
    (cheshire.core/generate-string $ {:pretty true})
    (spit path $)))

(defn write-showings-to-file []
  (map->file (external/movie-showings) "showings.json"))

(defn ensure-top-cast [m]
  (update m "topCast" (fnil identity [])))

(defn read-showings-from-file []
  (->> (slurp "showings.json")
       (json/read-str)
       (map ensure-top-cast)))
