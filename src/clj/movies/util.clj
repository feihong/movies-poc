(ns movies.util
  (:require [clojure.data.json :as json]
            [cheshire.core]
            [movies.external.core :as external]))


(defn map->file [m path]
  (as-> m $
    (cheshire.core/generate-string $ {:pretty true})
    (spit path $)))

(defn write-showings-to-file []
  (map->file (external/fetch-movie-showings) "showings.json"))
