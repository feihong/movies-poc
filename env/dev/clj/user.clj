(ns user
  (:require [luminus-migrations.core :as migrations]
            [clj-time.core :as t]
            [org.httpkit.client :as http]
            [cheshire.core :as json]
            [mount.core :as mount]
            [movies.config :refer [env]]
            [movies.core :refer [start-app]]
            [movies.util :as util]
            [movies.external.omdb :as omdb]
            [movies.external.gracenote :as gracenote]
            [movies.db.core :refer :all]
            [movies.cache :as cache]))

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

;;=============================================================================

; (cache/without-caching
;   (gracenote/fetch-movie-showings 3))
