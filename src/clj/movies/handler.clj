(ns movies.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [movies.layout :refer [error-page]]
            [movies.routes.home :refer [home-routes]]
            [movies.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [movies.env :refer [defaults]]
            [mount.core :as mount]
            [movies.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-formats))
      #'service-routes
      (route/not-found
        (:body
          (error-page {:status 404
                       :title "page not found"}))))))
