(ns movies.util
  (:require [clojure.pprint :as pprint]
            [clojure.string :as str]
            [movies.cache :as cache]))


(defn get-formatted-cache-item [id]
  "Fetch item from cache and reformat content for HTML rendering"
  (let [int-id (Integer/parseInt id)
        item (cache/get-cache-by-id int-id)
        content-str (with-out-str (-> item :content pprint/pprint))
        formatted-str (-> content-str
                          (str/replace "\n" "<br>")
                          (str/replace #"[ ]{2,}" #(str/replace % " " "&nbsp;")))]
    (assoc item :content formatted-str)))
