(ns movies.criteria
  (:require [clojure.string :refer [includes? lower-case]]))


(def countries ["china" "taiwan" "hong kong" "singapore"])

(def languages ["chinese" "mandarin" "cantonese"])

(def all-keywords (concat countries languages))

(defn text-contains? [text keywords]
  (let [lower-text (lower-case text)]
    (some #(includes? lower-text %) keywords)))

(defn assign-score [movie]
  "Assign a score to the given movie based on how it matches our criteria"
  (let [score (cond
                (nil? (movie :country)) 0
                (nil? (movie :language)) 0
                (text-contains? (movie :country) countries) 0
                (text-contains? (movie :language) languages) 0
                (text-contains? (movie :title) all-keywords) 1
                (text-contains? (movie :plot) all-keywords) 1
                :else 2)]
    (assoc movie :criteria-score score)))

(defn assign-scores [movies]
  "Assigns a score to each movie"
  (map assign-score movies))
