#pragma checksum "D:\ndk\Frontend\Frontend\Views\Home\TextDetails.cshtml" "{ff1816ec-aa5e-4d10-87f7-6f4963833460}" "5f51b67bb99874bb92edfd3fe0e902f28228765f"
// <auto-generated/>
#pragma warning disable 1591
[assembly: global::Microsoft.AspNetCore.Razor.Hosting.RazorCompiledItemAttribute(typeof(AspNetCore.Views_Home_TextDetails), @"mvc.1.0.view", @"/Views/Home/TextDetails.cshtml")]
[assembly:global::Microsoft.AspNetCore.Mvc.Razor.Compilation.RazorViewAttribute(@"/Views/Home/TextDetails.cshtml", typeof(AspNetCore.Views_Home_TextDetails))]
namespace AspNetCore
{
    #line hidden
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.AspNetCore.Mvc;
    using Microsoft.AspNetCore.Mvc.Rendering;
    using Microsoft.AspNetCore.Mvc.ViewFeatures;
#line 1 "D:\ndk\Frontend\Frontend\Views\_ViewImports.cshtml"
using Frontend;

#line default
#line hidden
#line 2 "D:\ndk\Frontend\Frontend\Views\_ViewImports.cshtml"
using Frontend.Models;

#line default
#line hidden
    [global::Microsoft.AspNetCore.Razor.Hosting.RazorSourceChecksumAttribute(@"SHA1", @"5f51b67bb99874bb92edfd3fe0e902f28228765f", @"/Views/Home/TextDetails.cshtml")]
    [global::Microsoft.AspNetCore.Razor.Hosting.RazorSourceChecksumAttribute(@"SHA1", @"da9a7313b162216ba4fd0de62e6951289e87a78f", @"/Views/_ViewImports.cshtml")]
    public class Views_Home_TextDetails : global::Microsoft.AspNetCore.Mvc.Razor.RazorPage<dynamic>
    {
        #pragma warning disable 1998
        public async override global::System.Threading.Tasks.Task ExecuteAsync()
        {
#line 1 "D:\ndk\Frontend\Frontend\Views\Home\TextDetails.cshtml"
  
    ViewData["Title"] = "TextDetails";

#line default
#line hidden
            BeginContext(47, 41, true);
            WriteLiteral("<h2>Результат обработки текста</h2>\r\n<h3>");
            EndContext();
            BeginContext(89, 19, false);
#line 5 "D:\ndk\Frontend\Frontend\Views\Home\TextDetails.cshtml"
Write(ViewData["Message"]);

#line default
#line hidden
            EndContext();
            BeginContext(108, 5, true);
            WriteLiteral("</h3>");
            EndContext();
        }
        #pragma warning restore 1998
        [global::Microsoft.AspNetCore.Mvc.Razor.Internal.RazorInjectAttribute]
        public global::Microsoft.AspNetCore.Mvc.ViewFeatures.IModelExpressionProvider ModelExpressionProvider { get; private set; }
        [global::Microsoft.AspNetCore.Mvc.Razor.Internal.RazorInjectAttribute]
        public global::Microsoft.AspNetCore.Mvc.IUrlHelper Url { get; private set; }
        [global::Microsoft.AspNetCore.Mvc.Razor.Internal.RazorInjectAttribute]
        public global::Microsoft.AspNetCore.Mvc.IViewComponentHelper Component { get; private set; }
        [global::Microsoft.AspNetCore.Mvc.Razor.Internal.RazorInjectAttribute]
        public global::Microsoft.AspNetCore.Mvc.Rendering.IJsonHelper Json { get; private set; }
        [global::Microsoft.AspNetCore.Mvc.Razor.Internal.RazorInjectAttribute]
        public global::Microsoft.AspNetCore.Mvc.Rendering.IHtmlHelper<dynamic> Html { get; private set; }
    }
}
#pragma warning restore 1591
