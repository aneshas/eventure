(ns eventure.app
  (:require [eventure.domain :as d])
  (:import (java.util UUID)))

(defn create-event [save-to-db cmd]
  (let [id (UUID/randomUUID)
        details (:event-details cmd)
        items (:event-items cmd)
        event (d/create-event id details items)]
    (save-to-db event)
    event))

(defn get-event [get-from-db id]
  (let [event (get-from-db id)]
    (dissoc event ::d/event-admin-hash)))

