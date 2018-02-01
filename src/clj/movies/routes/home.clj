(ns movies.routes.home
  (:require [movies.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [movies.showings :refer [movie-showings]]
            [movies.cache :as cache]
            [movies.util :as util]))

(defn home-page []
  (layout/render "home.html"))

(defn help-page []
  (layout/render
    "help.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn playing-now-page []
  (let [showings (movie-showings)
        match-count (->> showings
                         (filter :matches-criteria)
                         count)]
    (layout/render "playing-now.html" {:movies showings
                                       :match-count match-count})))

(defn cache-list-page []
  (layout/render "cache-list.html" {:items (cache/list-cache)}))

(defn cache-page [id]
  (layout/render "cache.html" {:item (util/get-cache-item id)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/help" [] (help-page))
  (GET "/about" [] (about-page))
  (GET "/playing-now" [] (playing-now-page))
  (GET "/cache" [] (cache-list-page))
  (GET "/cache/:id" [id] (cache-page id)))
