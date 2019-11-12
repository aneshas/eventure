(ns eventure.domain
  (:import (java.util Date UUID Base64))
  (:gen-class))

(defn- generate-admin-hash
  "A simple UUID for now"
  [id] (UUID/randomUUID))

(defn create-event
  "Creates new event with provided event id and admin hash"
  [id details items]
  {::event-id          id
   ::event-created-at  (Date.)
   ::event-admin-hash  (generate-admin-hash id)
   ::event-name        (:name details)
   ::event-host        (:host details)
   ::event-time        (:time details)
   ::event-description (:description details)
   ::event-items       (map (fn [i] {::item-id          (UUID/randomUUID)
                                     ::item-name        (:name i)
                                     ::item-description (:description i)}) items)})

(defn- check-if-admin [event admin-hash]
  (when
    (not= (::event-admin-hash event) admin-hash)
    (throw (Exception. "You are not an admin for this event."))))

(defn- create-attendee-hash [attendee]
  (.encodeToString (Base64/getEncoder) (.getBytes attendee)))

(defn- get-attendee-from-hash [attendee-hash]
  (String. (.decode (Base64/getDecoder) ^String attendee-hash)))

(defn share-event
  "Generates a guest share hash to be used to construct a personalized uri.
  Only admin can generate a hash."
  [event admin-hash invitee]
  (check-if-admin event admin-hash)
  (let [hash (create-attendee-hash invitee)
        new-event (assoc event ::invitees (conj (::invitees event) hash))]
    {:attendee-hash hash
     :event         new-event}))

; TODO - Define specs then test with spec
; TODO - Add couple of unit tests

(defn- item-claimed? [item] (contains? item ::item-claimed-by))

(defn- item-claimed-by? [item by]
  (and (item-claimed? item) (= by (::item-claimed-by item))))

(defn- claim-item [item attendee-hash]
  (let [attendee (get-attendee-from-hash attendee-hash)]
    (cond
      (item-claimed-by? item attendee) (dissoc item ::item-claimed-by)
      (item-claimed? item) item
      :else (assoc item ::item-claimed-by attendee))))

(defn- claim-item-with-id [item id attendee-hash]
  (if (= id (::item-id item))
    (claim-item item attendee-hash) item))

(defn- check-if-attendee [event name]
  (when
    (not (contains? (::invitees event) name))
    (throw (Exception. "You are not invited to this event."))))

(defn claim-event-item
  "Claims event item if not already claimed.
  Un-claims item if already claimed by the same person.
  If claimed by someone else action is ignored."
  [event item-id attendee-hash]
  (check-if-attendee event attendee-hash)
  (let [items (::event-items event)
        claimed-items (map #(claim-item-with-id % item-id attendee-hash) items)
        new-event (assoc event ::event-items claimed-items)] new-event))


