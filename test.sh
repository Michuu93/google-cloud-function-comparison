#!/bin/bash
printf "Please select function:\n"
cd functions || exit
select f in */; do test -n "$f" && break; echo ">>> Invalid Selection"; done
echo ${f}

printf "Please select region:\n"
select r in "europe-west3" "europe-central2" "us-central1"; do test -n "$r" && break; echo ">>> Invalid Selection"; done
echo ${r}

curl -v "https://${r}-$(gcloud config get-value project).cloudfunctions.net/${r}_${f}" -H "Authorization: bearer $(gcloud auth print-identity-token)"
