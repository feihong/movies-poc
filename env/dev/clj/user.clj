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
;; https://www.mediawiki.org/wiki/API:Main_page
;; https://www.mediawiki.org/wiki/API:Query
;; https://www.mediawiki.org/wiki/API:Main_page/zh
;; "title" : "Category:2018年电影"
;; prop categories|extlinks
;; https://en.wikipedia.org/wiki/A_Better_Tomorrow_2018
;; https://stackoverflow.com/questions/7638402/how-to-get-infobox-from-a-wikipedia-article-by-mediawiki-api
;; http://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=jsonfm&titles=Scary%20Monsters%20and%20Nice%20Sprites&rvsection=0
(def user-agent
  (str "ChineseMovies/1.1 (https://github.com/feihong/movies-poc; "
       " @feihonghsu)"))

(defn wikipedia [title]
  (let [api-url "https://en.wikipedia.org/w/api.php"
        qp {:titles title
            :format "json"
            :action "query"
            :prop "revisions"
            :rvprop "content"}]
    (-> @(http/get api-url {:query-params qp
                            :headers {"User-Agent" user-agent}})
        :body)))

(defn dump [title]
  (as-> (wikipedia title) $
        (json/decode $ true)
        (let [result (json/encode $ {:pretty true})]
          (println result)
          result)))
        ; (spit "sample.json" $)))

(defn print-page-content [title data]
  (->> :query
       :pages
       (fn [[_k page]] (= (:title page) title))
       first
       second))
       


; (dump "A Better Tomorrow 2018")
; (dump "英雄本色2018")
; (fetch-chinese-movie "A Better Tomorrow 2018")
