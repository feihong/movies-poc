(ns movies.criteria
  (:require [clojure.string :refer [includes? lower-case]]))


(def countries ["china" "taiwan" "hong kong" "singapore"])

(def languages ["chinese" "mandarin" "cantonese"])

(def all-keywords (concat countries languages))

(defn text-contains? [text keywords]
  "Check if given text contains one of the given keywords"
  (let [lower-text (lower-case text)]
    (some #(includes? lower-text %) keywords)))

(defn check-criteria-for-movie [movie]
  "Assign a score to the given movie based on how it matches our criteria"
  (let [matches (cond
                  (nil? (movie :country)) true
                  (nil? (movie :language)) true
                  (text-contains? (movie :country) countries) true
                  (text-contains? (movie :language) languages) true
                  (text-contains? (movie :title) all-keywords) true
                  (text-contains? (movie :plot) all-keywords) true
                  :else false)]
    (assoc movie :matches-criteria matches)))

(defn check-criteria [movies]
  "Assigns a score to each movie"
  (map check-criteria-for-movie movies))
