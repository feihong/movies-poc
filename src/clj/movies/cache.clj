(ns movies.cache
  (:require [clojure.data.json :as json]
            [org.httpkit.client :as http]
            [movies.db.core :as db]))


(def ^:dynamic *use-cache* true)

(defn fetch-json [key url options]
  (if *use-cache*
    (if-let [result (db/get-cache {:key key})]
      (json/read-str result)
      (let [body (-> @(http/get url options) :body)]
        (db/update-cache! {:key key :content body})
        (json/read-str body)))
    @(http/get url options)))

(defmacro without-caching [& body]
  `(binding [*use-cache* false]
     ~@body))
