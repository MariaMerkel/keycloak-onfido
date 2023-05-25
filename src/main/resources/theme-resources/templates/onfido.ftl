<script src="https://assets.onfido.com/web-sdk-releases/12.2.2/onfido.min.js"></script>
<link
        href="https://assets.onfido.com/web-sdk-releases/12.2.2/style.css"
        rel="stylesheet"
/>

<div id="onfido-mount"></div>

<#import "template.ftl" as layout>

<@layout.registrationLayout; section>
    <#if section = "form">
        <div id="container" style="display: none">
            <h1>Processing...</h1>

            <form id="kc-onfido-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post"></form>
        </div>
    </#if>
</@layout.registrationLayout>

<script type="application/javascript">
    onfidoOut = Onfido.init({
        token: '${sdk_token}',
        containerId: 'onfido-mount',
        useModal: true,
        isModalOpen: true,
        onComplete: function (data) {
            onfidoOut.setOptions({isModalOpen: false});
            document.getElementById('container').style.display = 'inline';
            document.getElementById('kc-onfido-login-form').submit();
        },
        steps: [{
            'type': 'face',
            'options': {
                requestedVariant: '${variant}',
                uploadFallback: false,
                photoCaptureFallback: false
            }
        }]
    });
</script>