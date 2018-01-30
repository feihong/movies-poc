(ns movies.routes.home
  (:require [movies.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [movies.showings :refer [movie-showings]]
            [movies.cache :as cache]))

(defn home-page []
  (layout/render "home.html"))

(defn help-page []
  (layout/render
    "help.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn playing-now-page []
  (layout/render "playing-now.html" {:movies (movie-showings)}))

(defn cache-list-page []
  (layout/render "cache-list.html" {:items (cache/list-cache)}))

(defn cache-page [id]
  (let [int-id (Integer/parseInt id)
        item (cache/get-cache-by-id int-id)
        content-str (with-out-str (-> item :content pprint/pprint))]
    (layout/render "cache.html" {:item item :content content-str})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/help" [] (help-page))
  (GET "/about" [] (about-page))
  (GET "/playing-now" [] (playing-now-page))
  (GET "/cache" [] (cache-list-page))
  (GET "/cache/:id" [id] (cache-page id)))
