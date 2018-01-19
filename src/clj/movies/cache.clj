(ns movies.cache
  (:require [cheshire.core :as json]
            [clj-time.core :as t]
            [org.httpkit.client :as http]
            [movies.db.core :as db]))


(def ^:dynamic *use-cache* true)

(defn request->json [url options]
  (-> @(http/get url options)
      :body
      (json/parse-string true)))

(defn update-cache [key content duration]
  (let [expires_at (t/plus (t/now) duration)
        key-str (if (string? key) key (str key))]
    (db/update-cache! {:key key-str
                       :content content
                       :expires_at expires_at})))

(defn fetch-json [key url options & {:keys [duration transform]
                                     :or {duration (t/hours 6)
                                          transform identity}}]
  (if *use-cache*
    (if-let [result (db/get-cache {:key key})]
      (:content result)
      (let [content (-> (request->json url options) transform)]
        (update-cache key content duration)
        content))
    (request->json url options)))

(defmacro without-caching [& body]
  `(binding [*use-cache* false]
     ~@body))
