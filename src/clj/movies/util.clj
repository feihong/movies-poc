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

(defn ensure-fields [m]
  "Ensure that topCast and directors fields are lists."
  (let [ensure-list (fnil identity [])]
    (-> m
        (update "topCast" ensure-list)
        (update "directors" ensure-list))))

(defn read-showings-from-file []
  (->> (slurp "showings.json")
       (json/read-str)
       (map ensure-fields)))
