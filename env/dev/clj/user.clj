(ns user
  (:require [luminus-migrations.core :as migrations]
            [clj-time.core :as t]
            [movies.config :refer [env]]
            [mount.core :as mount]
            [movies.core :refer [start-app]]
            [movies.util :as util]
            [movies.external :refer [movie-showings movie-meta]]
            [movies.db.core :refer :all]))

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
