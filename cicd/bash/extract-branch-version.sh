#!/usr/bin/env bash

branch_name="${GITHUB_REF#refs/heads/}"
var_name=$1

echo "branch_name=${branch_name}"
echo "var_name=${var_name}"

if [[ "$branch_name" =~ release/([0-9]+\.[0-9]+\.[0-9]+) ]]; then
  version="${BASH_REMATCH[1]}"
  echo "Extracted Version: $version"
  echo "${var_name}=${version}" >> "${GITHUB_ENV}"
else
  echo "${var_name}=${branch_name}" >> "${GITHUB_ENV}"
fi