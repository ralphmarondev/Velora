# ------------------------------------------------------------------------------
# Copyright (c) 2026 Ralph Maron Eda
# SPDX-License-Identifier: MIT
# ------------------------------------------------------------------------------
#
# Script: generate-keystore.sh
#
# Generates a new Android release keystore using the Java `keytool` utility.
#
# Requirements:
#   - Java Development Kit (JDK)
#   - `keytool` available in the system PATH
#
# Author:
#   Ralph Maron Eda
# ------------------------------------------------------------------------------

# The output keystore filename.
KEYSTORE_FILE="velora-release-key.keystore"

# The alias used to identify the generated key within the keystore.
KEY_ALIAS="velora"

# The cryptographic algorithm used to generate the key pair.
KEY_ALGORITHM="RSA"

# The RSA key size in bits.
KEY_SIZE=2048

# The certificate validity period in days.
# 10,000 days is approximately 27 years.
VALIDITY_DAYS=10000

keytool -genkeypair \
  -v \
  -keystore "$KEYSTORE_FILE" \
  -alias "$KEY_ALIAS" \
  -keyalg "$KEY_ALGORITHM" \
  -keysize "$KEY_SIZE" \
  -validity "$VALIDITY_DAYS"