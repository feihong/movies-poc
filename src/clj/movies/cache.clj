(ns movies.cache
  (:require [clj-time.core :as t]
            [cheshire.core :as json]
            [org.httpkit.client :as http]
            [movies.db.core :as db]))


(def ^:dynamic *use-cache* true)

(defn request->json [url options]
  (-> @(http/get url options)
      :body
      (json/decode true)))

(defn list-cache []
  (db/list-cache))

(defn get-cache-by-id [id]
  (db/get-cache-by-id {:id id}))

(defn get-cache [key]
  (let [key-str (if (string? key) key (str key))]
    (db/get-cache {:key key-str})))

(defn update-cache [key content duration]
  (let [expires_at (t/plus (t/now) duration)
        key-str (if (string? key) key (str key))]
    (db/update-cache! {:key key-str
                       :content content
                       :expires_at expires_at})))

(defn fetch-json [key url options & {:keys [duration]
                                     :or {duration (t/hours 6)}}]
  (if *use-cache*
    (if-let [result (get-cache key)]
      (:content result)
      (let [content (request->json url options)]
        (update-cache key content duration)
        content))
    (request->json url options)))

(defmacro without-caching [& body]
  `(binding [*use-cache* false]
     ~@body))
