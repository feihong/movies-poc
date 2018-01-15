(ns user
  (:require [luminus-migrations.core :as migrations]
            [movies.config :refer [env]]
            [mount.core :as mount]
            [movies.core :refer [start-app]]
            [org.httpkit.client :as http]
            [clj-time.core]
            [clj-time.format :as f]))


(defn start []
  (mount/start-without #'movies.core/repl-server))

(defn stop []
  (mount/stop-except #'movies.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))

(defn movie-showtimes []
  (let [url "http://data.tmsapi.com/v1.1/movies/showings"
        date-str (-> (f/formatters :date)
                     (f/unparse (clj-time.core/now)))
        params {:startDate date-str
                :api_key (-> env :gracenote :api-key)
                :lat 41.891377
                :lng -87.618997}
        res @(http/get url {:query-params params})]
    (:body res)))
