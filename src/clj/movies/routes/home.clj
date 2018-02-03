(ns movies.routes.home
  (:require [compojure.core :refer [defroutes context GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [movies.layout :as layout]
            [movies.showings :refer [movie-showings]]
            [movies.routes.cache :refer [cache-routes]]))

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



(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/help" [] (help-page))
  (GET "/about" [] (about-page))
  (GET "/playing-now" [] (playing-now-page))
  (context "/cache" [] (cache-routes)))
