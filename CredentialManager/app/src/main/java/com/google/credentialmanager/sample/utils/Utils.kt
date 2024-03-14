package com.google.credentialmanager.sample.utils

import android.util.Base64
import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.model.ByteString
import co.nstant.`in`.cbor.model.Map
import co.nstant.`in`.cbor.model.NegativeInteger
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util
import org.bouncycastle.jce.ECNamedCurveTable
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec

fun String.b64Decode(): ByteArray {
    return Base64.decode(this, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}

fun ByteArray.b64Encode(): String {
    return Base64.encodeToString(this, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE)
}
/**
 * Convert the user's public key, stored as String, to a java PublicKey
 */
fun String.toJavaPublicKey(): PublicKey {
    val decoded = CborDecoder.decode(this.b64Decode()).first() as Map
    val publicKeyX = decoded[NegativeInteger(-2)] as ByteString
    val publicKeyY = decoded[NegativeInteger(-3)] as ByteString
    val ecPoint = ECPoint(BigInteger(1, publicKeyX.bytes), BigInteger(1, publicKeyY.bytes))
    val params = ECNamedCurveTable.getParameterSpec("secp256r1")
    val ellipticCurve = EC5Util.convertCurve(params.curve, params.seed)
    val params2 = EC5Util.convertSpec(ellipticCurve, params)
    val keySpec = ECPublicKeySpec(ecPoint, params2)
    return KeyFactory.getInstance("EC").generatePublic(keySpec)
}