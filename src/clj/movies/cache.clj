(ns movies.cache
  (:require [org.httpkit.client :as http]
            [clj-time.core :as t]
            [movies.db.core :refer [get-cache, update-cache!]]))


(defn is-fresh [datetime]
  "Returns true if given datetime is less than 6 hours old"
  (let [later (t/plus datetime (t/hours 6))]
    (t/after? later (t/now))))

(defn fetch [url options]
  (let [result (get-cache {:url url})
        {:keys [content modified_at]} result]
    (if (and result (is-fresh modified_at))
      ; Database returns org.h2.jdbc.JdbcClob for content
      (-> content .getCharacterStream slurp)
      (let [fresh-content (-> @(http/get url options) :body)]
        (update-cache! {:url url
                        :content fresh-content
                        :modified_at (java.util.Date.)})
        fresh-content))))
