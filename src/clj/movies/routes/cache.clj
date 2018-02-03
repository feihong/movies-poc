(ns movies.routes.cache
  (:require [compojure.core :as compojure :refer [routes GET]]
            [ring.util.http-response :as response]
            [movies.layout :as layout]
            [movies.cache :as cache]
            [movies.db.core :as db]
            [movies.util :as util]))


(defn cache-list-page []
  (layout/render "cache-list.html" {:items (cache/list-cache)}))

(defn cache-page [id]
  (layout/render "cache.html" {:item (util/get-formatted-cache-item id)}))

(defn cache-delete [id]
  ; (layout/render "cache.html" {:item (util/get-cache-item id)}))
  (let [int-id (Integer/parseInt id)]
    (db/delete-cache! {:id int-id})
    (response/found "/cache")))

(defn cache-routes []
  (routes
    (GET "/" [] (cache-list-page))
    (GET "/:id" [id] (cache-page id))
    (GET "/:id/delete" [id] (cache-delete id))))
