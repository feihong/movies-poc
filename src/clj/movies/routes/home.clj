(ns movies.routes.home
  (:require [movies.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
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

(defn cache-page []
  (layout/render "cache.html" {:items (cache/list-cache)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/help" [] (help-page))
  (GET "/about" [] (about-page))
  (GET "/playing-now" [] (playing-now-page))
  (GET "/cache" [] (cache-page)))
